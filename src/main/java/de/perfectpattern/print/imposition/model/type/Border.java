package de.perfectpattern.print.imposition.model.type;

public class Border {

	private Long top;
	private Long bottom;
	private Long left;
	private Long right;

	public Border(final Long top,final Long bottom,final Long left,final Long right) {
		this.bottom=bottom;
		this.top=top;
		this.left=left;
		this.right=right;
	}

	public Border(final Long all) {
		this(all,all,all,all);
	}

	public Long getTop() {
		return this.top;
	}

	public Long getBottom() {
		return this.bottom;
	}

	public Long getLeft() {
		return this.left;
	}

	public Long getRight() {
		return this.right;
	}

	public Long getTop(final Orientation orientation) {
		switch (orientation) {
			case Rotate90:
				return this.left;
			case Rotate180:
				return this.bottom;
			case Rotate270:
				return this.right;
			default:
				return this.top;
		}
	}

	public Long getBottom(final Orientation orientation) {
		switch (orientation) {
			case Rotate90:
				return this.right;
			case Rotate180:
				return this.top;
			case Rotate270:
				return this.left;
			default:
				return this.bottom;
		}
	}

	public Long getLeft(final Orientation orientation) {
		switch (orientation) {
			case Rotate90:
				return this.bottom;
			case Rotate180:
				return this.right;
			case Rotate270:
				return this.top;
			default:
				return this.left;
		}
	}

	public Long getRight(final Orientation orientation) {
		switch (orientation) {
			case Rotate90:
				return this.top;
			case Rotate180:
				return this.left;
			case Rotate270:
				return this.bottom;
			default:
				return this.right;
		}
	}

}
