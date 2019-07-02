package it.fago.bench.simple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


/**
 * 
 * @author Stefano
 *
 */
public class JMHArraySearch {

	@State(Scope.Thread)
	public static class Elements {

		Random rnd;
		int[] array;
		int[] surelyPresent,randomlyChoosen;
		HashSet<Integer> lookup = new HashSet<Integer>();

		@Setup
		public void setup() {
			
			rnd = new Random();
			array = new int[64];
			surelyPresent= new int[8];
			randomlyChoosen = new int[8];
			
			for (int i = 0; i < array.length; i++) {
				array[i] = rnd.nextInt(65535);
				lookup.add(array[i]);
				if(i%8==0){
					surelyPresent[i==0?0:i/8]=array[i];
				}
			}
			for(int i=0; i<randomlyChoosen.length; i++){
				randomlyChoosen[i]=array[rnd.nextInt(64)];
			}
			System.out.println("Data: "+Arrays.toString(array));
			System.out.println("Milestone: "+Arrays.toString(surelyPresent));
			System.out.println("Random: "+Arrays.toString(randomlyChoosen));
		}

		@TearDown
		public void shutdown() {

		}
		
		public final void search(){
			final int len = array.length;
			final int toFind = surelyPresent[rnd.nextInt(8)];
			for (int i = 0; i < len ; i++) {
				if(array[i]==toFind){
					return;
				}
			}
		}
		
		public final void searchRnd(){
			final int len = array.length;
			final int toFind = randomlyChoosen[rnd.nextInt(8)];
			for (int i = 0; i < len ; i++) {
				if(array[i]==toFind){
					return;
				}
			}
		}
		
		public final void searchFail(){
			final int len = array.length;
			for (int i = 0; i < len ; i++) {
				if(array[i]==-1){
					return;
				}
			}
		}
		
		
		public final void searchBin(){
			final int toFind = surelyPresent[rnd.nextInt(8)];
			Arrays.binarySearch(array, toFind);
		}
		
		
		public final void searchBinRandom(){
			final int toFind = randomlyChoosen[rnd.nextInt(8)];
			Arrays.binarySearch(array, toFind);
		}
		
		
		public final void searchBinFail(){
			Arrays.binarySearch(array, -1);
		}
		
		public final void searchBySetRnd(){
			final int toFind = randomlyChoosen[rnd.nextInt(8)];
			lookup.contains(toFind);
		}
		
		public final void searchBySetFail(){
			lookup.contains(-1);
		}
		
	}//END

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputArray(Elements elem) throws InterruptedException {
		elem.search();
	}


	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputRandom(Elements elem) throws InterruptedException {
		elem.searchRnd();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputFail(Elements elem) throws InterruptedException {
		elem.searchFail();
	}
	
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputBin(Elements elem) throws InterruptedException {
		elem.searchBin();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputBinRnd(Elements elem) throws InterruptedException {
		elem.searchBinRandom();
	}
	
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputBinFail(Elements elem) throws InterruptedException {
		elem.searchBinFail();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputSetRnd(Elements elem) throws InterruptedException {
		elem.searchBySetRnd();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void measureThroughputSetFail(Elements elem) throws InterruptedException {
		elem.searchBySetFail();
	}
	
	public static void main(String[] args) throws RunnerException {
		
		Options opt = new OptionsBuilder()
				.include(JMHArraySearch.class.getSimpleName())
				.forks(1)
				.threads(4)
				.build();

		new Runner(opt).run();
	}

}
