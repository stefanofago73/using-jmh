package it.fago.bench.simple;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.profile.HotspotRuntimeProfiler;
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

	@State(Scope.Benchmark)
	public static class Elements {

		int[] array;
		HashSet<Integer> lookup = new HashSet<Integer>();


		@Param({"0","8","16","24","32","40","48","56"})
		public int mileStoneIdx;
		
		@Setup
		public void setup() {
			array = new int[64];		
			for (int i = 0; i < array.length; i++) {
				array[i] = ThreadLocalRandom.current().nextInt(65535);
				lookup.add(array[i]);
			}
			System.out.println("Data: " + Arrays.toString(array));
			System.out.println("Milestone: "+mileStoneIdx);
		}

		@TearDown
		public void shutdown() {

		}

		public final void search() {
			final int len = array.length;
			final int toFind = array[mileStoneIdx];
			for (int i = 0; i < len; i++) {
				if (array[i] == toFind) {
					return;
				}
			}
		}

		public final void searchFail() {
			final int len = array.length;
			for (int i = 0; i < len; i++) {
				if (array[i] == -1) {
					return;
				}
			}
		}
		
		public final void searchBin() {
			final int toFind = array[mileStoneIdx];
			Arrays.binarySearch(array, toFind);
		}

		public final void searchBinFail() {
			Arrays.binarySearch(array, -1);
		}

		public final void searchBySet() {
			final int toFind = array[mileStoneIdx];
			lookup.contains(toFind);
		}
		
		public final void searchBySetFail() {
			lookup.contains(-1);
		}
	
	}// END

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void measureThroughputArrayMileStone(Elements elem) throws InterruptedException {
		elem.search();
	}
	
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void measureThroughputArrayFail(Elements elem) throws InterruptedException {
		elem.searchFail();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void measureThroughputBin(Elements elem) throws InterruptedException {
		elem.searchBin();
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void measureThroughputBinFail(Elements elem) throws InterruptedException {
		elem.searchFail();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void measureThroughputSet(Elements elem) throws InterruptedException {
		elem.searchBySet();
	}
	

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	@CompilerControl(CompilerControl.Mode.DONT_INLINE)
	public void measureThroughputSetFail(Elements elem) throws InterruptedException {
		elem.searchBySetFail();
	}
	
	
	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder()
				.include(JMHArraySearch.class.getSimpleName())
				  .forks(1)
				  //.threads(4)
				  //.addProfiler(HotspotRuntimeProfiler.class)
			.build();

		new Runner(opt).run();
	}

}//END