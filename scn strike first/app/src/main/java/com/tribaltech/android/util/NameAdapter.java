package com.tribaltech.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.scnstrikefirst.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rmn.androidscreenlibrary.ASSL;

public class NameAdapter extends BaseAdapter {

	Context ctx;
	List<String[]> dataList;
	public int itemHeight;
	private static final int INDEX_ID = 0;
	private static final int INDEX_NAME = 1;
	private String field;
	ListView listView;
	Handler handler;

	public NameAdapter(Context ctx, List<String[]> list, int itemHeight,
                       String field, ListView listView) {
		this.ctx = ctx;
		this.dataList = list;
		this.itemHeight = itemHeight;
		this.field = field;
		handler = new Handler();
		this.listView = listView;
	}

	public void setContestList(List<String[]> contestList) {
		this.dataList = contestList;
	}

	private static class ViewHolder {
		TextView ballName;
		Button edit;
		Button delete;
		RelativeLayout rlt;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final String[] contest = dataList.get(position);
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ball_type_item, null);
			holder = new ViewHolder();
			holder.rlt = (RelativeLayout) convertView.findViewById(R.id.root);
			holder.ballName = (TextView) convertView
					.findViewById(R.id.ballName);
			holder.edit = (Button) convertView.findViewById(R.id.editSub);
			holder.delete = (Button) convertView.findViewById(R.id.deleteSub);
			holder.rlt.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, itemHeight));
			holder.rlt.setTag(holder);
			ASSL.DoMagic(holder.rlt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				askName(contest[INDEX_ID], contest[INDEX_NAME], position);
			}
		});

		holder.delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteBall(contest[INDEX_ID], position);
			}
		});

		holder.ballName.setText(contest[INDEX_NAME]);
		return convertView;
	}

	public void askName(final String ballId, final String ballName,
			final int position) {
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View promptsView = inflater.inflate(R.layout.input_layout, null);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ctx);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setCancelable(false);

		TextView label = (TextView) promptsView.findViewById(R.id.textView1);
		label.setText("Enter " + field + " Name");

		final EditText inputnumber = (EditText) promptsView
				.findViewById(R.id.pickedScore);
		inputnumber.setText(ballName);

		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String name = inputnumber.getText().toString();
						if (name.isEmpty()) {
							Toast.makeText(ctx, "Please enter a valid name.",
									Toast.LENGTH_SHORT).show();
							askName(ballId, ballName, position);
						} else {
							dialog.dismiss();
							addEdit(ballId, name, position);
						}
					}
				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alertDialogBuilder.show();
	}

	private void addEdit(String id, final String name, final int position) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(CommonUtil.TIMEOUT);
		client.post(
				Data.baseUrl
						+ "UserStat/AddEditEquipment"
						+ field
						+ "Name?token="
						+ CommonUtil.getAccessToken(ctx).replaceAll("[+]",
								"%2B") + "&apiKey=" + Data.apiKey + "&Id=" + id
						+ "&User" + field + "Name="
						+ name.replaceAll(" ", "%20"),
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String response) {
						try {
							if (position != -1) {
								dataList.get(position)[INDEX_NAME] = name;
								notifyDataSetChanged();
							} else {
								dataList.add(new String[] {
										new JSONObject(response)
												.getString("id"), name });
								notifyDataSetChanged();
								CommonUtil.setListViewHeightBasedOnChildren(
										listView, itemHeight);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable e) {
						String message = "Error performing operation.Please try again.";
						if (e.getMessage().equals("Conflict")) {
							message = "Ball name already exists.";
						}
						CommonUtil.commonGameErrorDialog(ctx, message + "");
						CommonUtil.loading_box_stop();
					}
				});
	}

	private void deleteBall(final String ballId, final int position) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
                    if(!CommonUtil.is_loading_showing()){
                        CommonUtil.loading_box(ctx,"Deleting...");
                    }
					HttpDelete httpDelete = new HttpDelete(Data.baseUrl
							+ "UserStat/DeleteEquipment"
							+ field
							+ "Name?token="
							+ CommonUtil.getAccessToken(ctx).replaceAll("[+]",
									"%2B") + "&apiKey=" + Data.apiKey + "&Id="
							+ ballId);
					HttpClient client = new DefaultHttpClient();
					final HttpResponse response = client.execute(httpDelete);
					if (response.getStatusLine().getStatusCode() == 200) {

						handler.post(new Runnable() {
							@Override
							public void run() {
                                CommonUtil.loading_box_stop();
								dataList.remove(position);
								notifyDataSetChanged();
								CommonUtil.setListViewHeightBasedOnChildren(
										listView, itemHeight);
							}
						});

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public List<String[]> getContestList() {
		return dataList;
	}

}