package ca.mcmaster.pdfparser.entity;

class CellPosition implements Comparable<CellPosition> {

	CellPosition(int row, int col) {
		this.row = row;
		this.col = col;
	}

	final int row, col;

	@Override public int hashCode() {
		return row + 101 * col;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CellPosition other = (CellPosition) obj;
		return row == other.row && col == other.col;
	}

	@Override public int compareTo(CellPosition other) {
		int rowdiff = row - other.row;
		return rowdiff != 0 ? rowdiff : col - other.col;
	}

}
