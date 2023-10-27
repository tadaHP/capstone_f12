package io.f12.notionlinkedblog.api.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DataApiController {
	private final ResourceLoader resourceLoader;

	@GetMapping(Endpoint.Api.REQUEST_FILE + "/{fileName}")
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable(value = "fileName") String fileName,
		HttpServletResponse response) throws IOException {
		String systemPath = System.getProperty("user.dir");
		String filePath = systemPath + "/" + fileName;

		Resource resource = resourceLoader.getResource("file:" + filePath);

		if (!resource.exists()) {
			throw new IllegalArgumentException(ExceptionMessages.FileExceptionsMessages.FILE_NOT_EXIST);
		}

		// 파일 다운로드 처리
		InputStream inputStream = new FileInputStream(resource.getFile());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);

		return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
	}

	@GetMapping(Endpoint.Api.REQUEST_IMAGE + "/{imageName}")
	public ResponseEntity<byte[]> getThumbnail(@PathVariable String imageName) {
		String systemPath = System.getProperty("user.dir");
		File imageFile = new File(systemPath + "/" + imageName);
		ResponseEntity<byte[]> result = null;

		try {
			HttpHeaders header = new HttpHeaders();
			header.add("Content-type", Files.probeContentType(imageFile.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(imageFile), header, HttpStatus.OK);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return result;
	}
}
