package com.olliespage.Feedback;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.*;
import android.content.Context;
import android.util.Log;

public class JSONFeedbackModel {
	public boolean hasDisturbance = false;
	public String modelName;
	public String modelDescription;
	public int maxLevel = 0;
	// try an array here, or move level into BlockDevice?
	public List<ArrayList<BlockDevice>> blockDevices = new ArrayList<ArrayList<BlockDevice>>();

	public JSONFeedbackModel(Context context, FeedbackModel systemModel, String assetName) {
		// first read JSON file into a string
		
		String jsonData = loadJSONAssetAsString(context,assetName+".json");
		try {
			JSONObject jsonDict = new JSONObject(jsonData);
			parseModel(jsonDict.getJSONArray("model"), systemModel, 0, false);
			hasDisturbance = jsonDict.optBoolean("hasDisturbance");
			modelName = jsonDict.optString("name", "Unknown Model");
			modelDescription = jsonDict.optString("description");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseModelObject(JSONObject object, FeedbackModel systemModel, int level, boolean subModel) {
		if(maxLevel < level) maxLevel = level;
		if(blockDevices.size() < level+1 && !subModel) blockDevices.add(new ArrayList<BlockDevice>());
		Iterator<String> itr = object.keys();
		while(itr.hasNext()) {
			String name = itr.next();
            Log.v("parseMO", "Block is: "+name);
            if(name.equals("model"))
            {
                // here we have an entire sub-model that must be parsed using parseModel
                FeedbackModel newModel = new FeedbackModel();
                parseModel(object.optJSONArray(name), newModel, 0, true);
                FeedbackModelBlockDevice newDevice = new FeedbackModelBlockDevice(name, newModel);
                systemModel.addBlockDevice(newDevice, level);
                if(!subModel)
                    blockDevices.get(level).add(newDevice);
                return;
            } else {
                JSONObject block = object.optJSONObject(name);
                if (block.has("block")) {
                    BlockDevice newDevice = new BlockDevice(name, block.optString("block"), level);
                    systemModel.addBlockDevice(newDevice, level);
                    if(!subModel)
                        blockDevices.get(level).add(newDevice);
                    Log.v("parseMO", name + " is a block with value: " + block.optDouble("block"));
                } else if (block.has("limitBlock")) {
                    // add the limit to the model and shiz
                    BlockDevice newDevice = new BlockDevice(name, block.optString("limitBlock"), level, 1);
                    systemModel.setLimitValue(newDevice.getDoubleValue());
                    if(!subModel)
                        blockDevices.get(level).add(newDevice);
                    Log.v("parseMO", name + " is a limit block with value: " + block.optDouble("limitBlock"));
                }
            }
		} // systemModel.limit resets after the while loop is left.
		Log.v("parseMO", "object is of length "+object.length());
	}
	
	private void parseModel(JSONArray jsonModel, FeedbackModel systemModel, int level, boolean subModel) {
		Log.v("JSON", "the length is: "+ jsonModel.length());
		for(int i=0; i < jsonModel.length(); i++) {
			if(jsonModel.optJSONObject(i) != null)
			{
				JSONObject element = jsonModel.optJSONObject(i);
				parseModelObject(element, systemModel, level, subModel); // pass responsibility on
			} else if(jsonModel.optJSONArray(i) != null)
			{
				JSONArray element = jsonModel.optJSONArray(i);
				// hehe, couldn't do this before, but I'm totally going to
				parseModel(element, systemModel, level+1, subModel); // send it back through itself
				// RECURSION RECURSES
			}
		}
	}
	
	private String loadJSONAssetAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("JSON", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("JSON", "Error closing asset " + name);
                }
            }
        }

        return null;
    }

}
