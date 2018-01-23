package com.github.giedomak.telepath.utilities

import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.GraphDatabase
import org.neo4j.driver.v1.Session


object Neo4jBenchmark {

    private val Q1 = "MATCH (a)-[:undergraduateDegreeFrom]->(b)<-[:subOrganizationOf]-(c)<-[:memberOf]-(d) RETURN COUNT(a)"
    private val Q2 = "MATCH (a)-[:advisor]->(b)-[:teacherOf]->(c)<-[:takesCourse]-(d) RETURN COUNT(a)"
    private val Q3 = "MATCH (a)<-[:headOf]-(b)-[:worksFor]->(c)<-[:subOrganizationOf]-(d) RETURN COUNT(a)"
    private val Q4 = "MATCH (a)<-[:headOf]-(b)-[:worksFor]->(c)-[:subOrganizationOf]->(d) RETURN COUNT(a)"

    private val Q5 = "MATCH (a)-[:advisor]->(b)-[:teacherOf]->(c)<-[:takesCourse]-(d)-[:advisor]->(e) RETURN COUNT(a)"
    private val Q6 = "MATCH (a)-[:undergraduateDegreeFrom]->(b)<-[:subOrganizationOf]-(c)<-[:memberOf]-(d)-[:advisor]->(e) RETURN COUNT(a)"
    private val Q7 = "MATCH (a)<-[:worksFor]-(b)-[:teacherOf]->(c)<-[:takesCourse]-(d)-[:advisor]->(e) RETURN COUNT(a)"

    private val Q8 = "MATCH (a)<-[:teacherOf]-(b)-[:undergraduateDegreeFrom]->(c)<-[:subOrganizationOf]-(d)<-[:memberOf]-(e)-[:advisor]->(f) RETURN COUNT(a)"
    private val Q9 = "MATCH (a)-[:advisor]->(b)-[:teacherOf]->(c)<-[:takesCourse]-(d)-[:advisor]->(e)-[:teacherOf]->(f) RETURN COUNT(a)"

    private val queries = listOf(Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9)

    @JvmStatic
    fun main(args: Array<String>?) {

        val driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "yay"))

        driver.session().use { session ->

            runQueries(session)

        }

        driver.close()

    }

    private fun runQueries(session: Session) {

        val means = mutableListOf<Double>()

        queries.forEach {
            means.add(runQuery(it, session))
            Logger.debug("Means: $means")
        }

    }

    private fun runQuery(query: String, session: Session): Double {

        val timings = mutableListOf<Long>()

        for (i in 1..20) {

            val start = System.currentTimeMillis()

            val result = session.run(query).next().values()
            val ms = System.currentTimeMillis() - start

            timings.add(ms)

            Logger.debug("Result in $ms ms: $result")

        }

        val mean = timings.sorted().drop(2).reversed().drop(2).average()
        Logger.debug("Mean: $mean")

        return mean

    }

}
