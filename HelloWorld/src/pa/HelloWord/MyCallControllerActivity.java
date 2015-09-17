package pa.HelloWord;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import com.android.internal.telephony.ITelephony;

public class MyCallControllerActivity extends Activity {
	/** Called when the activity is first created. */
	CheckBox blockAll_cb;// ,blockcontacts_cb;
	BroadcastReceiver CallBlocker;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;
	ListView lv;
	Set<String> allContact;

	private List<NameBean> items;
	List<NameBean> listArray;
	NameBean objItem;
	private NamesAdapter objAdapter = null;
	String allNum[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initviews();
		allContact = new TreeSet<String>();
		lv = (ListView) findViewById(R.id.listView1);

		blockAll_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (allNum == null) {
					blockAll_cb.setChecked(false);
					Toast.makeText(getApplicationContext(),
							"please Select Contact", 0).show();
					return;
				}

				if (isChecked) {
					Toast.makeText(getApplicationContext(), "If", 0).show();
					CallBlocker = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							// TODO Auto-generated method stub
							telephonyManager = (TelephonyManager) context
									.getSystemService(Context.TELEPHONY_SERVICE);
							// Java Reflections
							Class c = null;
							try {
								c = Class.forName(telephonyManager.getClass()
										.getName());
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Method m = null;
							try {
								m = c.getDeclaredMethod("getITelephony");
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							m.setAccessible(true);
							try {
								telephonyService = (ITelephony) m
										.invoke(telephonyManager);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							telephonyManager.listen(callBlockListener,
									PhoneStateListener.LISTEN_CALL_STATE);
						}// onReceive()

						PhoneStateListener callBlockListener = new PhoneStateListener() {
							public void onCallStateChanged(int state,
									String incomingNumber) {
								// StringBuilder sb = new StringBuilder();
								boolean flag = false;
								// for (int i = 0; i < allNum.length; i++) {
								// String s = allNum[0];
								// char ch[] = s.toCharArray();
								// for (int j = 0; j < ch.length; j++) {
								// if (ch[i] == '-') {
								// continue;
								// } else {
								// sb.append(ch[j]);
								// }
								// }
								// Log.d("^^^^^^^^^^^^^^^^^^^^^^^^^^^",
								// incomingNumber + "   ^^^^^^^  "
								// + allNum[i]
								// + "****************" + sb);
								// if (incomingNumber.equals(allNum[i])) {
								// flag = true;
								// break;
								// }
								// }
								// Toast.makeText(getApplicationContext(),
								// incomingNumber, 1).show();
								for (int i = 0; i < allNum.length; i++) {

									Log.d("^^^^^^^^^^^^^^^^^^^^^^^^^^^",
											incomingNumber + "   ^^^^^^^  "
													+ allNum[i]);
									//Toast.makeText(getApplicationContext(),
									//		incomingNumber+"       "+allNum[i], 1).show();
									if (incomingNumber.equals(allNum[i])) {
										flag = true;
									}
								}

								if (flag) {
									if (state == TelephonyManager.CALL_STATE_RINGING) {
										if (blockAll_cb.isChecked()) {
											try {
//												SmsManager smsManager = SmsManager
//														.getDefault();
//												smsManager
//														.sendTextMessage(
//																incomingNumber,
//																null,
//																"\nIs this text related to my event? Reply YES/NO",
//																null, null);

												telephonyService.endCall();
											} catch (RemoteException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									}
								} else {
									Toast.makeText(getApplicationContext(),
											incomingNumber, 1).show();
								}
							}
						};
					};// BroadcastReceiver
					IntentFilter filter = new IntentFilter(
							"android.intent.action.PHONE_STATE");
					registerReceiver(CallBlocker, filter);
				} else {
					Toast.makeText(getApplicationContext(), "Else", 0).show();
					unregisterReceiver(CallBlocker);
					CallBlocker = null;
				}
			}
		});

		Button contact = (Button) findViewById(R.id.button1);
		contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StringBuffer sb = new StringBuffer();
				for (NameBean bean : items) {

					if (bean.isSelected()) {
						sb.append(bean.getNumber());
						sb.append(",");
					}
				}

				String str = new String(sb);
				allNum = str.split(",");
				for (String s : allNum) {
					Toast.makeText(getApplicationContext(), s.toString(), 0)
							.show();
				}
				// List<String> li = new ArrayList<String>();
				// for (Object o : allContact) {
				// li.add(o.toString());
				// }
				// ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				// getApplicationContext(),
				// android.R.layout.simple_list_item_multiple_choice, li);
				//
				// lv.setAdapter(arrayAdapter);
			}
		});
		
		
		new MyTask().execute();
	}

	// --------------------------------------------------------------------------
	class MyTask extends AsyncTask<Void, Void, Void> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(MyCallControllerActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			items = displayContacts();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != pDialog && pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (null == items || items.size() == 0) {
				Toast.makeText(getApplicationContext(), "Data Not Found", 1)
						.show();
				MyCallControllerActivity.this.finish();
			} else {
				setAdapterToListview();
			}
			super.onPostExecute(result);
		}
	}

	// -----------------------------------------------------------------------------

	private List<NameBean> displayContacts() {
		listArray = new ArrayList<NameBean>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						objItem = new NameBean();
						objItem.setName(name);
						objItem.setNumber(phoneNo);
						listArray.add(objItem);
						//
						// Toast.makeText(MyCallControllerActivity.this,
						// "Name: " + name + ", Phone No: " + phoneNo,
						// Toast.LENGTH_SHORT).show();
					}
					pCur.close();
				}
			}
		}
		return listArray;
	}

	public void setAdapterToListview() {

		// Sort Data Alphabatical order
		Collections.sort(items, new Comparator<NameBean>() {

			@Override
			public int compare(NameBean lhs, NameBean rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});

		objAdapter = new NamesAdapter(MyCallControllerActivity.this, items);
		lv.setAdapter(objAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckBox chk = (CheckBox) view.findViewById(R.id.checkbox);
				NameBean bean = items.get(position);
				if (bean.isSelected()) {
					bean.setSelected(false);
					chk.setChecked(false);
				} else {
					bean.setSelected(true);
					chk.setChecked(true);
				}

			}
		});

	}

	public void initviews() {
		blockAll_cb = (CheckBox) findViewById(R.id.cbBlockAll);
		// blockcontacts_cb=(CheckBox)findViewById(R.id.cbBlockContacts);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}