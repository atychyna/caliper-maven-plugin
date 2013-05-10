def tmpdir = System.getProperty("java.io.tmpdir")

File paramBenchmark = new File("$tmpdir/BenchmarkWithParam")
paramBenchmark.deleteOnExit()
File testBenchmark = new File("$tmpdir/TestBenchmark")
testBenchmark.deleteOnExit()

assert paramBenchmark.isFile()
assert paramBenchmark.text == "1,2,3"

assert testBenchmark.isFile()
assert testBenchmark.text == "success"