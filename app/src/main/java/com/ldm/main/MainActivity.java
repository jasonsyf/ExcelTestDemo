package com.ldm.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ldm.db.DBHelper;
import com.ldm.excel.ExcelUtils;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements OnClickListener {
	private EditText mFoodEdt;
	private EditText mArticlesEdt;
	private EditText mTrafficEdt;
	private EditText mTravelEdt;
	private EditText mClothesEdt;
	private EditText mDoctorEdt;
	private EditText mRenQingEdt;
//	private EditText mBabyEdt;
//	private EditText mLiveEdt;
//	private EditText mOtherEdt;
	private EditText mRemarkEdt;
	private Button mSaveBtn;
	private Button mClearBtn;
	private File file;
	private String[] title = { "日期", "食物支出", "日用品项", "交通话费", "旅游出行", "穿着支出", "医疗保健", "人情客往", "备注说明" };
	private String[] saveData;
	private DBHelper mDbHelper;
	private ArrayList<ArrayList<String>>bill2List;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewsById();
		mDbHelper = new DBHelper(this);
		mDbHelper.open();
		bill2List=new ArrayList<ArrayList<String>>();
	}

	private void findViewsById() {
		mFoodEdt = (EditText) findViewById(R.id.family_bill_food_edt);
		mArticlesEdt = (EditText) findViewById(R.id.family_bill_articles_edt);
		mTrafficEdt = (EditText) findViewById(R.id.family_bill_traffic_edt);
		mTravelEdt = (EditText) findViewById(R.id.family_bill_travel_edt);
		mClothesEdt = (EditText) findViewById(R.id.family_bill_clothes_edt);
		mDoctorEdt = (EditText) findViewById(R.id.family_bill_doctor_edt);
		mRenQingEdt = (EditText) findViewById(R.id.family_bill_laiwang_edt);
//		mBabyEdt = (EditText) findViewById(R.id.family_bill_baby_edt);
//		mLiveEdt = (EditText) findViewById(R.id.family_bill_live_edt);
//		mOtherEdt = (EditText) findViewById(R.id.family_bill_other_edt);
		mRemarkEdt = (EditText) findViewById(R.id.family_bill_remark_edt);
		mSaveBtn = (Button) findViewById(R.id.family_bill_save);
		mSaveBtn.setOnClickListener(this);
		mClearBtn = (Button) findViewById(R.id.family_bill_clear);
		mClearBtn.setOnClickListener( this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.family_bill_save) {
			saveData = new String[] { new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
					mFoodEdt.getText().toString().trim(), mArticlesEdt.getText().toString().trim(),
					mTrafficEdt.getText().toString().trim(), mTravelEdt.getText().toString().trim(),
					mClothesEdt.getText().toString().trim(), mDoctorEdt.getText().toString().trim(),
					mRenQingEdt.getText().toString().trim(),  mRemarkEdt.getText().toString().trim() };
			if (canSave(saveData)) {
				ContentValues values = new ContentValues();
				values.put("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				values.put("food", mFoodEdt.getText().toString());
				values.put("use", mArticlesEdt.getText().toString());
				values.put("traffic", mTrafficEdt.getText().toString());
				values.put("travel", mTravelEdt.getText().toString());
				values.put("clothes", mClothesEdt.getText().toString());
				values.put("doctor", mDoctorEdt.getText().toString());
				values.put("laiwang", mRenQingEdt.getText().toString());
				values.put("remark", mRemarkEdt.getText().toString());
				long insert = mDbHelper.insert("family_bill", values);
				if (insert > 0) {
					initData();
				}
			} else {
				Toast.makeText(this, "请填写任意一项内容", Toast.LENGTH_SHORT).show();
			}
		}else if (v.getId() == R.id.family_bill_clear) {
			if (FileUtils.isFileExists(getSDPath() + "/Family")) {
				LogUtils.i("path",FileUtils.isFileExists(getSDPath() + "/Family")+"存在文件");
//				FileUtils.deleteDir(getSDPath() + "/Family");
				AppUtils.cleanAppData(getSDPath() + "/Family");
				ToastUtils.showShort("清除成功");
			} else {
				ToastUtils.showShort("清除失败");
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void initData() {
		file = new File(getSDPath() + "/Family");
		makeDir(file);
		ExcelUtils.initExcel(file.toString() + "/newBill.xls", title);
		ExcelUtils.writeObjListToExcel(getBillData(), getSDPath() + "/Family/newBill.xls", this);
	}

	private ArrayList<ArrayList<String>> getBillData() {
		Cursor mCrusor = mDbHelper.exeSql("select * from family_bill");
		while (mCrusor.moveToNext()) {
			ArrayList<String> beanList=new ArrayList<String>();
			beanList.add(mCrusor.getString(1));
			beanList.add(mCrusor.getString(2));
			beanList.add(mCrusor.getString(3));
			beanList.add(mCrusor.getString(4));
			beanList.add(mCrusor.getString(5));
			beanList.add(mCrusor.getString(6));
			beanList.add(mCrusor.getString(7));
			beanList.add(mCrusor.getString(8));
			beanList.add(mCrusor.getString(9));
			bill2List.add(beanList);
		}
		mCrusor.close();
		return bill2List;
	}

	public static void makeDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		String dir = sdDir.toString();
		return dir;

	}

	private boolean canSave(String[] data) {
		boolean isOk = false;
		for (int i = 0; i < data.length; i++) {
			if (i > 0 && i < data.length) {
				if (!TextUtils.isEmpty(data[i])) {
					isOk = true;
				}
			}
		}
		return isOk;
	}
}
