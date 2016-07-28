package com.artifex.mupdf;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

public class MuPDFPageView extends PageView {
	private final MuPDFCore mCore;
	public static int pdfSizeX;
	public static int pdfSizeY;
	public static int pdfPatchX;
	public static int pdfPatchY;
	public static int pdfPatchWidth;
	public static int pdfPatchHeight;
	public MuPDFPageView(Context c, MuPDFCore core, Point parentSize) {
		super(c, parentSize);
		mCore = core;
	}

	public int hitLinkPage(float x, float y) {
		// Since link highlighting was implemented, the super class
		// PageView has had sufficient information to be able to
		// perform this method directly. Making that change would
		// make MuPDFCore.hitLinkPage superfluous.
		float scale = mSourceScale*(float)getWidth()/(float)mSize.x;
		float docRelX = (x - getLeft())/scale;
		float docRelY = (y - getTop())/scale;
//		Log.v("info", "Page--->",docRelX);
		return mCore.hitLinkPage(mPageNumber, docRelX, docRelY);
	}

	@Override
	protected void drawPage(Bitmap bm, int sizeX, int sizeY,
			int patchX, int patchY, int patchWidth, int patchHeight) {
		mCore.drawPage(mPageNumber, bm, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
		pdfSizeX = sizeX;
		pdfSizeY = sizeY;
		pdfPatchX = patchX;
		pdfPatchY = patchY;
		pdfPatchWidth = patchWidth;
		pdfPatchHeight = patchHeight;
	}

	@Override
	protected LinkInfo[] getLinkInfo() {
		return mCore.getPageLinks(mPageNumber);
	}
	
}
