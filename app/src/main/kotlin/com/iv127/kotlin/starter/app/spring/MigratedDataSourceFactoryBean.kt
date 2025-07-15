package com.iv127.kotlin.starter.app.spring

import com.iv127.kotlin.starter.app.migrateDataSource
import org.springframework.beans.factory.FactoryBean
import javax.sql.DataSource

class MigratedDataSourceFactoryBean : FactoryBean<DataSource> {
    lateinit var unmigratedDataSource: DataSource
    override fun getObject() =
        unmigratedDataSource.also(::migrateDataSource)

    override fun getObjectType() =
        DataSource::class.java

    override fun isSingleton() =
        true
}
