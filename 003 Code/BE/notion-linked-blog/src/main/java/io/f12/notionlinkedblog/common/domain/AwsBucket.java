package io.f12.notionlinkedblog.common.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsBucket {
	@Value("${application.bucket.name}")
	private String bucket;
	@Value("${cloud.aws.region.static}")
	private String region;

	public String makeFileUrl(String fileFullName) {
		if (fileFullName == null) {
			return "https://" + bucket + ".s3." + region + ".amazonaws.com/profile/DefaultProfile.png";
		}
		return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileFullName;
	}

}