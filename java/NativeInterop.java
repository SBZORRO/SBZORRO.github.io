package com.shumu.xiehe;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class NativeInterop {
  public static void main(String[] args) throws Throwable {
    try (Arena arena = Arena.ofConfined()) {
      // Load the shared library
      Linker linker = Linker.nativeLinker();
      SymbolLookup libLookup = linker.defaultLookup();
      SymbolLookup lib = SymbolLookup.libraryLookup(
          "./hello.so", Arena.ofAuto());

      // Lookup the 'sayHello' function
      MethodHandle sayHello = linker.downcallHandle(
          lib.find("loop").orElseThrow(),
          FunctionDescriptor.of(ValueLayout.JAVA_INT));

      // Allocate a C string
//            MemorySegment cString = arena.allocateUtf8String("Java");

      // Call the C function
      sayHello.invoke();
    }
  }
}
