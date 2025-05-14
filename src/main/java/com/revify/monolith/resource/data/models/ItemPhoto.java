package com.revify.monolith.resource.data.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@RequiredArgsConstructor
@Document(collection = "item_photo")
public class ItemPhoto extends CustomFile {
	private String description;
	private String orderId;

	public String prepareFileKey() {
		String fileKey = Strings.EMPTY;
		if (getInitialFileName() != null && !getInitialFileName().isEmpty()) {
			return getInitialFileName().replaceAll("[@#()!%^&+{}~\\[\\]_`]", "_") + "_" + orderId;
		}
		return orderId;
	}
}