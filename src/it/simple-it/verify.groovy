File result = new File("/tmp/result");

assert result.isFile()
assert result.text.toLong() > 0
