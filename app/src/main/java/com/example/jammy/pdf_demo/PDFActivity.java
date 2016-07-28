package com.example.jammy.pdf_demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.ReaderView;
import com.artifex.mupdf.SavePdf;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Jammy on 2016/6/23.
 */
public class PDFActivity extends Activity {

    @Bind(R.id.readerView)
    ReaderView readerView;

    @Bind(R.id.rl_back)
    RelativeLayout rlBack;
    @Bind(R.id.rl_sign)
    RelativeLayout rlSign;
    @Bind(R.id.rl_update)
    RelativeLayout rlUpdate;
    @Bind(R.id.rl_clear)
    RelativeLayout rlClear;
    @Bind(R.id.rl_save)
    RelativeLayout rlSave;

    boolean isUpdate = false;
    String in_path;
    String out_path;
    String update_path;
    PopupWindow popupWindow;
    SignatureView signatureView;
    boolean iBack = false;
    float density; //屏幕分辨率密度
    int first = 1;
    int back_first = 0;
    String file_id;
    ProgressDialog progressDialog;
    MuPDFCore muPDFCore;
    Save_Pdf save_pdf;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        ButterKnife.bind(this);

        View screenView = this.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //计算分辨率密度
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;


        in_path = Environment.getExternalStorageDirectory().getPath() + "/123.pdf";
        out_path = in_path.substring(0, in_path.length() - 4) + "1.pdf";


        try {
            muPDFCore = new MuPDFCore(in_path);//PDF的文件路径
            readerView.setAdapter(new MuPDFPageAdapter(this, muPDFCore));
            View view = LayoutInflater.from(this).inflate(R.layout.signature_layout, null);
            signatureView = (SignatureView) view.findViewById(R.id.qianming);
            readerView.setDisplayedViewIndex(0);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
            rlSign.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if (rlSave.getVisibility() == View.GONE) {
                        popupWindow.showAsDropDown(rlSign, 0, 0);
                        rlSave.setVisibility(View.VISIBLE);
                        rlClear.setVisibility(View.VISIBLE);
                        rlUpdate.setVisibility(View.VISIBLE);
                    } else {
                        popupWindow.dismiss();
                        signatureView.clear();
                        rlSave.setVisibility(View.GONE);
                        rlClear.setVisibility(View.GONE);
                        rlUpdate.setVisibility(View.GONE);
                    }
                }
            });
            rlSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float scale = readerView.getmScale();///得到放大因子
                    SavePdf savePdf = new SavePdf(in_path, out_path);
                    savePdf.setScale(scale);
                    savePdf.setPageNum(readerView.getDisplayedViewIndex() + 1);

                    savePdf.setWidthScale(1.0f * readerView.scrollX / readerView.getDisplayedView().getWidth());//计算宽偏移的百分比
                    savePdf.setHeightScale(1.0f * readerView.scrollY / readerView.getDisplayedView().getHeight());//计算长偏移的百分比


                    savePdf.setDensity(density);
                    Bitmap bitmap = Bitmap.createBitmap(signatureView.getWidth(), signatureView.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    signatureView.draw(canvas);
                    savePdf.setBitmap(bitmap);
                    save_pdf = new Save_Pdf(savePdf);
                    save_pdf.execute();
                    popupWindow.dismiss();
                    iBack = true;
                    rlSave.setVisibility(View.GONE);
                    rlClear.setVisibility(View.GONE);
                    rlUpdate.setVisibility(View.GONE);
                    rlBack.setVisibility(View.VISIBLE);
                    ///显示隐藏probar
                    progressDialog = ProgressDialog.show(PDFActivity.this, null, "正在存储...");
                    signatureView.clear();
                }
            });


            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (iBack) {
                            if (back_first == 1) {
                                muPDFCore = new MuPDFCore(getIntent().getExtras().getString("inPath"));
                                first = 1;
                                in_path = getIntent().getExtras().getString("inPath");
                                out_path = in_path.substring(0, in_path.length() - 4) + "1.pdf";
                            } else {
                                muPDFCore = new MuPDFCore(out_path);
                                String temp = in_path;
                                in_path = out_path;
                                out_path = temp;
                            }
                            readerView.setmScale(1.0f);
                            readerView.setDisplayedViewIndex(readerView.getDisplayedViewIndex());
                            iBack = false;
                            if (rlSave.getVisibility() == View.VISIBLE) {
                                rlSave.setVisibility(View.GONE);
                                rlClear.setVisibility(View.GONE);
                                rlUpdate.setVisibility(View.GONE);
                                signatureView.clear();
                            }
                            rlBack.setVisibility(View.GONE);
                            back_first--;
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PDFActivity.this);
                            builder.setTitle("无法返回");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除缓冲的存储
                                    dialog.cancel();
                                }
                            }).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            rlClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signatureView.clear();
                }
            });
            rlUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PDFActivity.this,"这里没有用到",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    * 用于存储的异步,并上传更新
    * */
    class Save_Pdf extends AsyncTask {

        SavePdf savePdf;

        public Save_Pdf(SavePdf savePdf) {
            this.savePdf = savePdf;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            savePdf.addText();
            if (first == 1) {
                update_path = in_path.substring(0, in_path.length() - 4) + ".pdf";
                in_path = in_path.substring(0, in_path.length() - 4) + "2.pdf";
                first++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(PDFActivity.this, "存储完成", Toast.LENGTH_SHORT).show();
            try {
                muPDFCore = new MuPDFCore(out_path);
                readerView.setAdapter(new MuPDFPageAdapter(PDFActivity.this, muPDFCore));

                String temp = in_path;
                in_path = out_path;
                out_path = temp;
                readerView.setmScale(1.0f);
                readerView.setDisplayedViewIndex(readerView.getDisplayedViewIndex());
                progressDialog.dismiss();
                back_first++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回按钮，退出时删除那两个文件
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否退出？");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除缓冲的存储
                PDFActivity.this.finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (first != 1) {
            File file = new File(in_path);
            File file1 = new File(out_path);
            File file2 = new File(update_path);
            if (file.exists()) file.delete();
            if (file1.exists()) file1.delete();
            if (file2.exists() && isUpdate) file2.delete();
        }
        save_pdf.cancel(true);
    }
}
