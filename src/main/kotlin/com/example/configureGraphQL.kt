package com.example

import com.apurebase.kgraphql.GraphQL
import com.example.dao.DAOFacade
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureGraphQL() {
    val dao: DAOFacade by inject()

    install(GraphQL) {
        playground = true
        schema {
            query("articles") {
                resolver { -> dao.allArticles() }
            }
            query("article") {
                resolver { id: Int -> dao.article(id) }
            }
            mutation("addNewArticle") {
                resolver { title: String, body: String ->
                    dao.addNewArticle(title, body)
                }
            }
            mutation("editArticle") {
                resolver { id: Int, title: String, body: String ->
                    dao.editArticle(id, title, body)
                }
            }
            mutation("deleteArticle") {
                resolver { id: Int ->
                    dao.deleteArticle(id)
                }
            }
        }
    }
}