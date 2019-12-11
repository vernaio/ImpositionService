package de.perfectpattern.print.imposition.service.imposition.layout.label;

import de.perfectpattern.print.imposition.util.DimensionUtil;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Label to display the latest end time of a sheet.
 */
public class LatestEndTimeLabel extends CaptionLabel {

	private static final DateTimeFormatter DATE_FORMAT;
	private static final DateTimeFormatter DAY_DATE_FORMAT;

	static {
		DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd' 'HH:mm:ss.SSS' 'XX").withZone(ZoneOffset.UTC);
		DAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd").withZone(ZoneOffset.UTC);
	}

	public LatestEndTimeLabel(final Long latestEndTime) {
		super(
			DimensionUtil.mm2dtp(50),
			"LET",
			DAY_DATE_FORMAT.format(Instant.ofEpochMilli(latestEndTime))
		);
	}

}
