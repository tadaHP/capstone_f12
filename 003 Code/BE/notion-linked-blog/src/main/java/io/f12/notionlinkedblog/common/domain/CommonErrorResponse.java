package io.f12.notionlinkedblog.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonErrorResponse {
	private String errorMassage;
	private int errorCode;
}
