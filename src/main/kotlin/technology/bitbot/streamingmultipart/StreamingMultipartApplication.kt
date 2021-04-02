package technology.bitbot.streamingmultipart

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.util.*


@SpringBootApplication
class StreamingMultipartApplication

fun main(args: Array<String>) {
	runApplication<StreamingMultipartApplication>(*args)
}


@Controller
class Controller {

	@GetMapping("/stream")
	@CrossOrigin
	fun streaming(): ResponseEntity<StreamingResponseBody> {
		val uuid = UUID.randomUUID().toString()
		val streamingResponseBody = StreamingResponseBody {
			for(i in 1..50) {
				val message = ObjectMapper().writeValueAsString(Message("info", "Hello Streaming $i")).toByteArray()
				val headers = "\r\n----$uuid\r\n" +
						"Content-Type: application/json\r\n" +
						"Content-Length: " + message.size + "\r\n\r\n"
				it.write(headers.toByteArray())
				it.write(message)
				it.write("\r\n".toByteArray())
				it.flush();
				Thread.sleep(300)
			}
		}
		return ResponseEntity.ok()
				.header("Content-Type", "${MediaType.MULTIPART_MIXED_VALUE}; boundary=--$uuid")
				.body(streamingResponseBody)
	}
}

data class Message(var level:String, var message:String)