package th.in.ffc.googlemap.directions;
/*package th.in.ffc.googlemap.lib;

GMapV2Direction md;
Document doc;
ArrayList<String> rout_list = null;

if (md == null || doc == null){
				Toast.makeText(getActivity(), "à¸•à¹?à¸­à¸?à¹?à¸?à¹?à¸?à¸²à¸?à¹?à¸«à¸¡à¸”à¸?à¸²à¸£ \"à¸«à¸²à¹€à¸ªà¹?à¸?à¸—à¸²à¸?\" à¸?à¹?à¸­à¸?à¸–à¸¶à¸?à¸?à¸°à¹?à¸?à¹?à¸?à¸²à¸?à¹?à¸”à¹?", Toast.LENGTH_SHORT).show();
				break;
			}else {

				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

				ListView modeList = new ListView(mActivity);
				// String[] stringArray = new String[] { "Bright Mode",
				// "Normal Mode" };

				rout_list = md.getList(doc);
								String dist = md.getDistanceText(doc);
				String duration = md.getDurationText(doc);
				String distance = rout_list.remove(0);
				String time = rout_list.remove(0);
				ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
						mActivity, android.R.layout.simple_list_item_1,
						android.R.id.text1, rout_list);
				modeList.setAdapter(modeAdapter);

				LinearLayout title = new LinearLayout(getActivity());
				title.setOrientation(LinearLayout.VERTICAL);

				TextView l1 = new TextView(getActivity());
				l1.setText("à¸?à¸³à¹?à¸?à¸°à¸?à¸³à¸?à¸²à¸£à¹€à¸”à¸´à¸?à¸—à¸²à¸?");
				l1.setTextAppearance(getActivity(), R.style.AppTheme);
				l1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				l1.setTextColor(getResources().getColor(R.color.holo_blue_dark));
				TextView l2 = new TextView(getActivity());
				l2.setText(distance+" "+time);
				l2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				l2.setTextColor(getResources().getColor(R.color.holo_orange_dark));

				title.addView(l1);
				title.addView(l2);

				title.setPadding(10, 10, 0, 10);

				builder.setView(modeList);
				builder.setCustomTitle(title);
				builder.create().show();
			}*/