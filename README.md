# ImpositionService
This Service creates printable PDFs based on sPrintOne XMLs.

## Configurations
Some options of the service can be configured using environment variables:

| ENV | Description | Datatype | Default |
| --- | --- | --- | --- |
| SHEET_BLEED_MM | Bleed of the Sheet PDF in millimeters. | Integer | 0 |
| HIDE_LABELS | Flag to hide labels on the sheet. | Boolean | false |
| BOX_MARK_TO_FINAL_TRIM_THRESHOLD | Threshold for clipped bleed to allow Magenta Box | Long | 0 |
