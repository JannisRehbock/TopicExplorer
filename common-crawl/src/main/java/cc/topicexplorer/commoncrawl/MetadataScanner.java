package cc.topicexplorer.commoncrawl;

// Java classes
import java.io.IOException;
import java.net.URI;

// Apache Project classes
import org.apache.log4j.Logger;

// Hadoop classes
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.commoncrawl.warc.WARCFileInputFormat;

import org.archive.io.ArchiveReader;

/**
 * A metadata scanner for the CommonCrawl archive. Based on Chris Stephens' <chris@commoncrawl.org>
 * ExampleMetadataDomainPageCount.java and Stephen Merity's WARCTagCounter.java
 * @author Florian Luecke
 */
public class MetadataScanner extends Configured implements Tool {

    private static final Logger LOG = Logger.getLogger(MetadataScanner.class);

    /**
     * Implements the map function for MapReduce.
     */
    public static class MetadataScannerMapper extends Mapper<Text, ArchiveReader, Text, Text> {

        // implement the main "map" function
        @Override
        public void map(Text key, ArchiveReader value, Context context) throws IOException, InterruptedException {
        }
    }


    /**
     * Implmentation of Tool.run() method, which builds and runs the Hadoop job.
     *
     * @param  args command line parameters, less common Hadoop job parameters stripped
     *              out and interpreted by the Tool class.
     * @return      0 if the Hadoop job completes successfully, 1 if not.
     */
    @Override
    public int run(String[] args) throws Exception {

        String outputPath = null;
        String configFile = null;

        // Read the command line arguments.
        if (args.length <  1) {
            throw new IllegalArgumentException("Example JAR must be passed an output path.");
        }

        outputPath = args[0];

        if (args.length >= 2) {
            configFile = args[1];
        }

        // Read in any additional config parameters.
        if (configFile != null) {
            LOG.info("adding config parameters from '"+ configFile + "'");
            this.getConf().addResource(configFile);
        }


        // Creates a new job configuration for this Hadoop job.
        Job job = new Job(this.getConf());

        /**
         * TODO: make inputpath configurable
         */
        String inputPath = "common-crawl/crawl-data/CC-MAIN-2014-23/segments/"
            + "1404776400583.60/warc/"
            + "CC-MAIN-20140707234000-00000-ip-10-180-212-248.ec2.internal.warc.gz";

        job.setJarByClass(MetadataScanner.class);

        // Scan the provided input path for ARC files.
        LOG.info("setting input path to '"+ inputPath + "'");
        FileInputFormat.addInputPath(job, new Path(inputPath));

        // Delete the output path directory if it already exists.
        LOG.info("clearing the output path at '" + outputPath + "'");

        FileSystem fs = FileSystem.get(new URI(outputPath), this.getConf());

        if (fs.exists(new Path(outputPath))) {
            fs.delete(new Path(outputPath), true);
        }

        // Set the path where final output 'part' files will be saved.
        LOG.info("setting output path to '" + outputPath + "'");
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        FileOutputFormat.setCompressOutput(job, false);

        // Set which InputFormat class to use.
        job.setInputFormatClass(WARCFileInputFormat.class);

        // Set which OutputFormat class to use.
        job.setOutputFormatClass(TextOutputFormat.class);

        // Set the output data types.
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Set which Mapper and Reducer classes to use.
        job.setMapperClass(MetadataScanner.MetadataScannerMapper.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    /**
     * Main entry point that uses the {@link ToolRunner} class to run the example
     * Hadoop job.
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MetadataScanner(), args);
        System.exit(res);
    }
}
