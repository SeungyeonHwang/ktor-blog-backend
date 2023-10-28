package com.example.plugins

import com.example.dao.*
import com.example.models.Article
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.io.*

fun Application.configureRouting() {
    val dao: DAOFacade = DAOFacadeCacheImpl(
        DAOFacadeImpl(),
        File(environment.config.property("storage.ehcacheFilePath").getString())
    ).apply {
        runBlocking {
            if(allArticles().isEmpty()) {
                addNewArticle("The drive to develop!", "...it's what keeps me going.")
            }
        }
    }

    routing {
        articleRouting(dao)
    }
}

fun Route.articleRouting(dao: DAOFacade) {
    route("/articles") {
        get {
            call.respond(dao.allArticles())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Article ID")
                return@get
            }

            val article = dao.article(id)
            if (article == null) {
                call.respond(HttpStatusCode.NotFound, "Article not found")
            } else {
                call.respond(article)
            }
        }

        post {
            val article = call.receive<Article>()
            val addedArticle = dao.addNewArticle(article.title, article.body)
            if (addedArticle != null) {
                call.respond(HttpStatusCode.Created, addedArticle)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Article ID")
                return@put
            }

            val article = call.receive<Article>()
            val updated = dao.editArticle(id, article.title, article.body)

            if (updated) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Article not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Article ID")
                return@delete
            }

            val deleted = dao.deleteArticle(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Article not found")
            }
        }
    }
}
