package ca.mcmaster.pdfparser.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import technology.tabula.Rectangle;
import technology.tabula.RectangularTextContainer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
@Slf4j
public class Cell extends RectangularTextContainer<TextChunk> {

    private boolean placeholder = false;
    private List<TextChunk> textElements;
    @Getter @Setter
	private int holdX = 1, holdY = 1;
    @Getter @Setter
    private int lineNum, colNum;
    private static final String PLACEHOLDER_FORMAT = "[[PLACEHOLDER] line: %s, col: %s]";
    private static final String CELL_FORMAT = "[[TEXT] text: %s, line: %d, col: %d]";

    public Cell(Point2D topLeft, Point2D bottomRight) {
        super((float) topLeft.getY(), (float) topLeft.getX(), (float) (bottomRight.getX() - topLeft.getX()), (float) (bottomRight.getY() - topLeft.getY()));
        this.setPlaceholder(false);
        this.setTextElements(new ArrayList<TextChunk>());
    }

	@Override
	public String getText(boolean useLineReturns) {
		if (this.textElements.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Collections.sort(this.textElements, Rectangle.ILL_DEFINED_ORDER);
		double curTop = this.textElements.get(0).getTop();
		for (TextChunk tc : this.textElements) {
			if (useLineReturns && tc.getTop() > curTop) {
				sb.append('\r');
			}
			sb.append(tc.getText());
			curTop = tc.getTop();
		}
		return sb.toString().trim();
	}

	public String getText() {
		return getText(true);
	}

	public boolean isPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(boolean placeholder) {
		this.placeholder = placeholder;
	}

	public List<TextChunk> getTextElements() {
		return textElements;
	}

	public void setTextElements(List<TextChunk> textElements) {
		this.textElements = textElements;
	}

	@Override
	public String toString(){
        StringBuilder sb = new StringBuilder();
        int lineNum = getLineNum();
        int colNum = getColNum();
        sb.append(placeholder ? String.format(PLACEHOLDER_FORMAT, lineNum, colNum):
                String.format(CELL_FORMAT, getText(), lineNum, colNum));
        return sb.toString();
    }
}
