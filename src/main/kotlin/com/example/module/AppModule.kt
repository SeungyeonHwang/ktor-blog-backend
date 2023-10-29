package com.example.module

import org.koin.dsl.module
import com.example.dao.DAOFacade
import com.example.dao.DAOFacadeImpl

val appModule = module {
    single<DAOFacade> { DAOFacadeImpl() }
}
