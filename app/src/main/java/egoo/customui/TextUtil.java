package egoo.customui;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;

public class TextUtil {
	private int mTextPosx = 0;// x����
	private int mTextPosy = 0;// y����
	private int mTextWidth = 0;// ���ƿ��
	private int mTextHeight = 0;// ���Ƹ߶�
	private int mFontHeight = 0;// ��������߶�
	private int mPageLineNum = 0;// ÿһҳ��ʾ������
	private int mCanvasBGColor = 0;// ������ɫ
	private int mFontColor = 0;// ������ɫ
	private int mAlpha = 0;// Alphaֵ
	private int mRealLine = 0;// �ַ�����ʵ������
	private int mCurrentLine = 0;// ��ǰ��
	private int mTextSize = 0;// �����С
	private String mStrText = "";
	private Vector mString = null;
	private Paint mPaint = null;

	public TextUtil(String StrText, int x, int y, int w, int h, int bgcolor,
			int textcolor, int alpha, int textsize) {
		mPaint = new Paint();
		mString = new Vector();
		this.mStrText = StrText;
		this.mTextPosx = x;
		this.mTextPosy = y;
		this.mTextWidth = w;
		this.mTextHeight = h;
		this.mCanvasBGColor = bgcolor;
		this.mFontColor = textcolor;
		this.mAlpha = alpha;
		this.mTextSize = textsize;
	}

	public void InitText() {
		mString.clear();// ���Vector
		// �Ի������Ե�����
		// mPaint.setARGB(this.mAlpha, Color.red(this.mFontColor), Color
		// .green(this.mFontColor), Color.blue(this.mFontColor));
		mPaint.setTextSize(this.mTextSize);
		mPaint.setColor(Color.BLUE);
		this.GetTextIfon();
	}

	public void GetTextIfon() {
		char ch;
		int w = 0;
		int istart = 0;
		FontMetrics fm = mPaint.getFontMetrics();// �õ�ϵͳĬ����������
		mFontHeight = (int) (Math.ceil(fm.descent - fm.top) + 2);// �������߶�
		mPageLineNum = mTextHeight / mFontHeight;// �������
		int count = this.mStrText.length();
		for (int i = 0; i < count; i++) {
			ch = this.mStrText.charAt(i);
			float[] widths = new float[1];
			String str = String.valueOf(ch);
			mPaint.getTextWidths(str, widths);
			if (ch == '\n') {
				mRealLine++;// ��ʵ��������һ
				mString.addElement(this.mStrText.substring(istart, i));
				istart = i + 1;
				w = 0;
			} else {
				w += (int) Math.ceil(widths[0]);
				if (w > this.mTextWidth) {
					mRealLine++;// ��ʵ��������һ
					mString.addElement(this.mStrText.substring(istart, i));
					istart = i;
					i--;
					w = 0;
				} else {
					if (i == count - 1) {
						mRealLine++;// ��ʵ��������һ
						mString.addElement(this.mStrText.substring(istart,
								count));
					}
				}
			}
		}
	}

	public void DrawText(Canvas canvas) {
		for (int i = this.mCurrentLine, j = 0; i < this.mRealLine; i++, j++) {
			if (j > this.mPageLineNum) {
				break;
			}
			canvas.drawText((String) (mString.elementAt(i)), this.mTextPosx,
					this.mTextPosy + this.mFontHeight * j, mPaint);
		}
	}

	public boolean KeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (this.mCurrentLine > 0) {
				this.mCurrentLine--;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if ((this.mCurrentLine + this.mPageLineNum) < (this.mRealLine - 1)) {
				this.mCurrentLine++;
			}
		}
		return false;
	}
}
