package org.mentionbattle.configuration

import org.json.JSONObject
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun CreateConfiguration(path : Path) : Configuration {
    if (!Files.exists(path)) {
        throw FileNotFoundException("The configuration file does not exist")
    }
    val result = JSONObject(Files.readAllLines(path).joinToString(separator = ""))

    val port = result["port"] as Int
    val snaPort = result["snaPort"] as Int
    val snaUrl  = result["snaUrl"] as String
    val snaKey : String = result["snaKey"] as String
    return Configuration(port, snaPort, snaUrl, snaKey)
}