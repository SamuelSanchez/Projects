import java.io.FileReader;
import java.io.BufferedReader;

public class EMExperimenter {
  private double[][] d;

  /**
   * Initializes an EMExperimenter object with classlabels and data.
   *
   * @param d the parameters
   */
  public EMExperimenter(double[][] d) {
    this.d = d;
  }

  /**
   * Initializes an empty EMExperimenter
   */
  public EMExperimenter() {
    this.d = null;
  }

  /**
   * Initializes an EMExperimenter from data in a file
   *
   * @param filename the filename to read
   */
  public EMExperimenter(String filename) {
    try {
      read_data(filename);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
	  
	args = new String[2];
	args[0] = "clustering.csv";
	args[1] = "7";

    if (args.length < 2) {
      System.err.println("Usage: EMExperimenter csv_data_file number_of_clusters");
      return;
    }

    try {
      EMExperimenter exp = new EMExperimenter(args[0]);
      Integer clusters = Integer.parseInt(args[1]);
      double threshold = 0.00001;

//      GaussianMixture gmm = new GaussianMixture(clusters, exp.getData());
      GaussianMixture gmm = new GaussianMixture(clusters, exp.getData(), false, true);
      gmm.init();
      gmm.find_EM(threshold);
      gmm.print();
      // If you like you may print the cluster centroids as well.
      System.out.println(clusters + "," + gmm.getLikelihood());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Access the data used in the EM process
   *
   * @return the data
   */
  private double[][] getData() {
    return d;
  }

  /**
   * Reads data from a filename
   * file format:
   * <p/>
   * line 1
   * rows,cols
   * <p/>
   * line 2-rows+1
   * csv data
   *
   * @param filename The filename
   * @throws Exception if something goes wrong with the read
   */
  private void read_data(String filename) throws Exception {
    BufferedReader inFile = new BufferedReader(new FileReader(filename));

    String[] dims = inFile.readLine().split(",");

    if (dims.length != 2) {
      throw new Exception("Error: malformed dimensions line");
    }

    this.d = new double[Integer.parseInt(dims[0])][Integer.parseInt(dims[1])];

    String str;
    int j = 0;
    while ((str = inFile.readLine()) != null) {
      String[] data = str.split(",");
      for (int i = 0; i < data.length; i++) {
        this.d[j][i] = Double.parseDouble(data[i]);
      }
      j++;
    }
    return;
  }
}
