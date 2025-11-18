package org.joon.appLinker.server


import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.joon.appLinker.constant.PlayerMessage
import org.joon.appLinker.util.Firebase
import java.util.UUID


class ApiServer(private val port: Int) {
    private val server = Server(port)

    fun start() {
        val context = ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        server.handler = context

        context.addServlet(PlayerServlet::class.java, "/players")
        context.addServlet(CheckServlet::class.java, "/check/*")
        println("API 서버가 실행되었습니다.")
        server.start()
    }

    fun stop() {
        println("API 서버가 종료되었습니다.")
        server.stop()
    }

    class PlayerServlet : HttpServlet() {
        override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
            val players = Bukkit.getOnlinePlayers().map { it.uniqueId.toString() }
            val response = """
                {
                    "count": ${players.size},
                    "uuids": ${players.joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]")}
                }
            """.trimIndent()

            resp.status = 200
            resp.characterEncoding = "UTF-8"
            resp.contentType = "application/json"
            resp.writer.use { it.write(response) }
        }
    }

    class CheckServlet : HttpServlet() {
        override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
            val uuid = extractUUID(req, resp) ?: return
            if (Firebase.checkReceivedReward(uuid.toString())) {
                alreadyCheck(resp)
                return
            }
            val player = Bukkit.getPlayer(uuid)
            if (player != null && player.isOnline) {
                giveReward(player)
                Firebase.recordRewardGiven(uuid.toString())
                giveResponse(resp, player)
            } else {
                playerOfflineResponse(resp)
            }
        }

        private fun extractUUID(req: HttpServletRequest, resp: HttpServletResponse): UUID? {
            val uuidStr = req.pathInfo?.removePrefix("/")?.trim()
            return try {
                UUID.fromString(uuidStr)
            } catch (e: IllegalArgumentException) {
                resp.status = 400
                resp.contentType = "application/json"
                resp.writer.write("""{"error": "잘못된 UUID입니다."}""")
                null
            }
        }

        private fun alreadyCheck(resp: HttpServletResponse) {
            resp.status = 409
            resp.contentType = "application/json"
            resp.writer.write("""{"status": "fail", "message": "이미 보상을 지급받은 플레이어입니다."}""")
        }

        private fun giveReward(player: Player) {
            Bukkit.getScheduler().runTask(
                JavaPlugin.getProvidingPlugin(this::class.java),
                Runnable {
                    val reward = ItemStack(Material.DIAMOND, 3)
                    player.inventory.addItem(reward)
                    player.sendMessage(PlayerMessage.PLAYER_REWARD)
                })
        }

        private fun giveResponse(resp: HttpServletResponse, player: Player) {
            resp.status = 200
            resp.contentType = "application/json"
            resp.writer.write(
                """
                    {"status": "success",
                     "message": "${player.name}에게 보상이 지급되었습니다."}
                    """.trimIndent()
            )
        }

        private fun playerOfflineResponse(resp: HttpServletResponse) {
            resp.status = 404
            resp.contentType = "application/json"
            resp.writer.write("""{"status": "fail", "message": "플레이어가 온라인이 아닙니다."}""")
        }
    }
}