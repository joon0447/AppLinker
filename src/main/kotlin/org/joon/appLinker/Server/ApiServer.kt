package org.joon.appLinker.Server


import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bukkit.Bukkit
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler


class ApiServer(private val port: Int) {
    private val server = Server(port)

    fun start() {
        val context = ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        server.handler = context

        context.addServlet(PlayerServlet::class.java, "/player")
        println("API 서버가 실행되었습니다.")
        server.start()
    }

    fun stop() {
        println("API 서버가 종료되었습니다.")
        server.stop()
    }

    class PlayerServlet: HttpServlet() {
        override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
            val players = Bukkit.getOnlinePlayers().map {it.uniqueId.toString()}
            val response = """
                {
                    "count": ${players.size},
                    "uuids": ${players.joinToString(prefix="[\"", separator="\", \"", postfix="\"]")}
                }
            """.trimIndent()

            resp.status = 200
            resp.characterEncoding = "UTF-8"
            resp.contentType = "application/json"
            resp.writer.use { it.write(response) }
        }
    }
}