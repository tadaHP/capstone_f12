package io.f12.notionlinkedblog.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import redis.embedded.RedisServer;

@Configuration
@Profile("dev")
public class EmbeddedRedisConfig {
	@Value("${spring.redis.port}")
	private int port;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServer() throws IOException {
		redisServer = RedisServer.builder()
			.port(isRedisRunning() ? findAvailablePort() : port)
			.setting("maxmemory 128M")
			.build();
		redisServer.start();
	}

	private int findAvailablePort() throws IOException {
		for (int port = 10000; port <= 65535; port++) {
			Process process = executeGrepProcessCommand(port);
			if (!isRunning(process)) {
				return port;
			}
		}
		throw new IllegalStateException("Not Found Available port: 10000 ~ 65535");
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}

	private boolean isRedisRunning() throws IOException {
		return isRunning(executeGrepProcessCommand(port));
	}

	private Process executeGrepProcessCommand(int port) throws IOException {
		String linuxCommand = String.format("netstat -nat | grep LISTEN|grep %d", port);
		String windowsCommand = String.format("netstat -ano | find \"LISTEN\"| find \"%d\"", port);
		String[] linux = {"/bin/sh", "-c", linuxCommand};
		String[] windows = {"cmd.exe", "/c", windowsCommand};

		return Runtime.getRuntime().exec(SystemUtils.IS_OS_WINDOWS ? windows : linux);
	}

	private boolean isRunning(Process process) {
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}
		} catch (Exception e) {
		}

		return StringUtils.hasText(pidInfo.toString());
	}
}
