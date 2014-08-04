package com.olliespage.Feedback;

import com.olliespage.Feedback.views.GraphView;
import com.olliespage.Feedback.views.interfaces.GraphViewInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GraphActivity extends Activity implements GraphViewInterface {
	private FeedbackModel systemModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		Intent intent = getIntent();
		systemModel = (FeedbackModel) intent.getSerializableExtra("model");
		GraphView leftGraph = (GraphView) findViewById(R.id.inputGraph);
		GraphView rightGraph = (GraphView)findViewById(R.id.outputGraph);
		leftGraph.delegate = this; // set self to left view
		rightGraph.delegate = this;
	}

	@Override
	public double getMaxValue() {
// FIXME: this should have a value for the disturbance from teh main interface...
		return systemModel.calculateOutputForInput(10, 0);
	}

	@Override
	public double getMinValue() {
// FIXME: this should have a value for the disturbance from teh main interface...
		return systemModel.calculateOutputForInput(-10, 0);
	}

	@Override
	public double getLimitValue() {
		return systemModel.getLimitValue();
	}

	@Override
	public double getOutputvDisturbace() {
		return systemModel.outputVdisturbanceGradient();
	}
	
	public void returnButtonClicked(View view)
	{
		this.onBackPressed();
	}
}
