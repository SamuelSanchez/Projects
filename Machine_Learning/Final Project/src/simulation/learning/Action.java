package simulation.learning;

import simulation.tool.Global;


public class Action {
		private Global.ACTION action;
		private double confidence;
		
		public Action(){
			this(Global.ACTION.UNKNOWN,-1);
		}
		
		public Action(Global.ACTION action, double confidence){
			this.setAction(action);
			this.setConfidence(confidence);
		}

		public Global.ACTION getAction() {
			return action;
		}

		public void setAction(Global.ACTION action) {
			this.action = action;
		}

		public double getConfidence() {
			return confidence;
		}

		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}
		
		public void print(){
			System.out.println(toString());
		}
		
		public String toString(){
			return "Action[ " + action.toString() + " ] - Confidence [ " + confidence + " ]";
		}
}
