package com.github.ahappypie

import picocli.CommandLine
import picocli.CommandLine.{Command, Parameters}

import java.nio.file.{Files, Paths}
import java.util.concurrent.Callable
import com.sksamuel.avro4s.json.JsonToAvroConverter

@Command(name = "json2avsc", mixinStandardHelpOptions = true, version = Array("json2avsc 0.0.1"),
  description = Array("Takes example JSON payload file and returns generated Avro Schema"))
class JSONToAvroConverter extends Callable[Int] {
  @Parameters(index = "0", description = Array("The file whose payload to convert."))
  private var file: String = null
  @Parameters(index = "1", description = Array("Namespace of converted schema, like 'com.example.namespace'"))
  private var namespace: String = null
  @Parameters(index = "2", description = Array("Class of converted schema, like 'MyClass'. Will result in 'com.example.namespace.MyClass'"))
  private var className: String = null


  override def call(): Int = {
    val fileContents = Files.readString(Paths.get(file))
    val converter = new JsonToAvroConverter(namespace)
    val schema = converter.convert(className, fileContents)
    printf(schema.toString(true))
    0
  }
}

object JSONToAvroConverter extends App {
  val exitCode = new CommandLine(new JSONToAvroConverter()).execute(args:_*)
  System.exit(exitCode)
}