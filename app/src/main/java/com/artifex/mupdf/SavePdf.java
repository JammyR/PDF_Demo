package com.artifex.mupdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;


/**
 * Created by Jammy on 2016/6/23.
 */
public class SavePdf {


    public void setWidthScale(float widthScale) {
        this.widthScale = widthScale;
    }

    public void setHeightScale(float heightScale) {
        this.heightScale = heightScale;
    }

    float widthScale;
    float heightScale;
    String inPath;/////当前的PDF地址
    String outPath;////要输出的PDF地址
    private int pageNum;/////签名所在的页码
    private Bitmap bitmap;//////签名图像
    private float scale;
    private float density;  ///手机屏幕的分辨率密度

    /**
     * 设置放大比例
     *
     * @param scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }


    /**
     * 设置分辨率密度
     *
     * @param density
     */
    public void setDensity(float density) {
        this.density = density;
    }

    /**
     * 设置嵌入的图片
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * 设置需要嵌入的页面
     *
     * @param pageNum
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public SavePdf(String inPath, String outPath) {
        this.inPath = inPath;
        this.outPath = outPath;
    }

    /**
     * 将图片加入PDF并保存
     */
    public void addText() {
        try {
            PdfReader reader = new PdfReader(inPath, "PDF".getBytes());///打开要写入的PDF
            FileOutputStream outputStream = new FileOutputStream(outPath);//设置涂鸦后的PDF
            PdfStamper stamp;
            stamp = new PdfStamper(reader, outputStream);
            PdfContentByte over = stamp.getOverContent(pageNum);//////用于设置在第几页打印签名
            byte[] bytes = Bitmap2Bytes(bitmap);
            Image img = Image.getInstance(bytes);//将要放到PDF的图片传过来，要设置为byte[]类型
            com.lowagie.text.Rectangle rectangle = reader.getPageSize(pageNum);
            img.setAlignment(1);
            ////乘显示值和原始值的比值
            img.scaleAbsolute(363 * 1.0f * density / 2 / scale * rectangle.getWidth() / (bitmap.getWidth() / 2), 557 * 1.0f * density / 2 / scale * rectangle.getWidth() / (bitmap.getWidth() / 2));


            //设置相对位置
            img.setAbsolutePosition(rectangle.getWidth() * widthScale, rectangle.getHeight() * heightScale);
            over.addImage(img);
            stamp.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将BitMap转换为Bytes
     *
     * @param bm
     * @return
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static float mmTopx(float mm) {
        mm = (float) (mm * 3.33);
        return mm;
    }

}
