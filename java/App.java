package com.shumu.xiehe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.Charset;
import java.util.List;

//import org.jnetpcap.Pcap;
//import org.jnetpcap.PcapException;
//import org.jnetpcap.PcapHeader;
//import org.jnetpcap.PcapIf;

public class App {

  private static String LINE_SEPARATOR = System.getProperty(
      "line.separator");

  @FunctionalInterface
  public interface NativeCallback {
    void nativeCallback(MemorySegment user, MemorySegment header,
        MemorySegment packet);
  }

  class Handler {
    static void handle(MemorySegment user, MemorySegment header,
        MemorySegment packet) {
      System.out.print("Packet: ");
      System.out.println(
          packet.reinterpret(Integer.MAX_VALUE)
              .getString(54, Charset.defaultCharset()));
//      MemorySegment cs = packet.getAtIndex(ValueLayout.ADDRESS, 0);
//      cs = cs.reinterpret(4);
//      System.out.println(cs.getString(0, Charset.defaultCharset()));
//      try (Arena arena = Arena.ofConfined()) {
//        
//        MemorySegment z = packet.get(ValueLayout.ADDRESS, 0); // size = 0, scope = always alive
//        MemorySegment ptr = z.reinterpret(16, arena, null); // size = 4, scope = arena.scope()
//        int x = ptr.getAtIndex(ValueLayout.JAVA_INT, 3); // ok
//        System.out.println(x);
//      }

//      return 0;
    }
  }

  public static void main(String[] args) throws Throwable {

//    Linker linker = Linker.nativeLinker();
////    SymbolLookup lib = linker.defaultLookup();
//    SymbolLookup lib = SymbolLookup
//        .libraryLookup("C:\\Windows\\System32\\Npcap\\wpcap", Arena.ofAuto());
////    lib.find("C:\\Windows\\System32\\Npcap").orElseThrow();

    Linker linker = Linker.nativeLinker();
    SymbolLookup libLookup = linker.defaultLookup();
    SymbolLookup lib = SymbolLookup.libraryLookup(
        "./hello.so",
        Arena.ofAuto());

    MethodHandle sayHello = linker.downcallHandle(
        lib.find("loop_handler").orElseThrow(),
        FunctionDescriptor.of(
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS));

    MethodHandle hdl = MethodHandles.lookup().findStatic(
        Handler.class,
        "handle",
        MethodType.methodType(
            void.class,
            MemorySegment.class,
            MemorySegment.class,
            MemorySegment.class));

    // Create a Java description of a C function implemented by a Java method
    FunctionDescriptor hdlDesc = FunctionDescriptor.ofVoid(
        ValueLayout.ADDRESS,
//        ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS);

    // Create function pointer for qsortCompare
    MemorySegment hdlFunc = linker.upcallStub(
        hdl,
        hdlDesc,
        Arena.ofAuto());

//    try (Arena arena = Arena.ofConfined()) {

//      // Allocate off-heap memory and store unsortedArray in it                
//      MemorySegment array = arena.allocateArray(ValueLayout.JAVA_INT,
//          unsortedArray);

    // Call qsort        
    sayHello.invoke(hdlFunc);

//      // Access off-heap memory
//      sorted = array.toArray(ValueLayout.JAVA_INT);
//    }
  }

//  public static void test(String[] args) throws PcapException, IOException {
//
////    Linker linker = Linker.nativeLinker();
//////    SymbolLookup lib = linker.defaultLookup();
////    SymbolLookup lib = SymbolLookup.libraryLookup("C:\\Windows\\System32\\Npcap\\wpcap",  Arena.ofAuto());
//////    System.loadLibrary("wpcap");
////
//////    lib.find("C:\\Windows\\System32\\Npcap").orElseThrow();
////    
////    MethodHandle sayHello = linker.downcallHandle(
////        lib.find("loop").orElseThrow(),
////        FunctionDescriptor.of(ValueLayout.JAVA_INT));
//
//    List<PcapIf> devices = Pcap.findAllDevs();
//    for (PcapIf pif : devices) {
//      System.out.println(pif);
//    }
//
//    int nifIdx = 0;
//    while (true) {
//      System.out.print(
//          "Select a device number to capture packets, or enter 'q' to quit > ");
//
//      String input;
//      if ((input = read()) == null) {
//        continue;
//      }
//
//      if (input.equals("q")) {
//        return;
//      }
//
//      try {
//        nifIdx = Integer.parseInt(input);
//        if (nifIdx < 0 || nifIdx >= devices.size()) {
//          System.out.print("Invalid input." + LINE_SEPARATOR);
//          continue;
//        } else {
//          break;
//        }
//      } catch (NumberFormatException e) {
//        System.out.print("Invalid input." + LINE_SEPARATOR);
//        continue;
//      }
//    }
//
//    try (Pcap pcap = Pcap.create(devices.get(nifIdx))) {
//      pcap.activate();
//      pcap.setFilter(pcap.compile("tcp port 9998", true));
//      pcap.loop(-1, (String msg, PcapHeader header, byte[] packet) -> {
//
//        System.out.println(msg);
//        System.out.println(new String(packet));
//      }, null);
//    }
//
//  }

  static String read() throws IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in));
    return reader.readLine();
  }

//  public static void test() {
// // 1. Find foreign function on the C library path
//    Linker linker          = Linker.nativeLinker();
//    SymbolLookup stdlib    = linker.defaultLookup();
//    MethodHandle radixsort = linker.downcallHandle(stdlib.find("radixsort"), null);
//    // 2. Allocate on-heap memory to store four strings
//    String[] javaStrings = { "mouse", "cat", "dog", "car" };
//    // 3. Use try-with-resources to manage the lifetime of off-heap memory
//    try (Arena offHeap = Arena.ofConfined()) {
//        // 4. Allocate a region of off-heap memory to store four pointers
//        MemorySegment pointers
//            = offHeap.allocate(ValueLayout.ADDRESS, javaStrings.length);
//        // 5. Copy the strings from on-heap to off-heap
//        for (int i = 0; i < javaStrings.length; i++) {
//            MemorySegment cString = offHeap.allocateFrom(javaStrings[i]);
//            pointers.setAtIndex(ValueLayout.ADDRESS, i, cString);
//        }
//        // 6. Sort the off-heap data by calling the foreign function
//        radixsort.invoke(pointers, javaStrings.length, MemorySegment.NULL, '\0');
//        // 7. Copy the (reordered) strings from off-heap to on-heap
//        for (int i = 0; i < javaStrings.length; i++) {
//            MemorySegment cString = pointers.getAtIndex(ValueLayout.ADDRESS, i);
//            javaStrings[i] = cString.reinterpret(0).getString(0);
//        }
//    } // 8. All off-heap memory is deallocated here
//    assert Arrays.equals(javaStrings,
//                         new String[] {"car", "cat", "dog", "mouse"});  // true
//  }

  /** Example instance */
//  void main() throws PcapException {
//    /* Pcap capture file to read */
//    final String PCAP_FILE = "pcaps/HTTP.cap";
//
//    /* Make sure we have a compatible Pcap runtime installed */
//    Pcap.checkPcapVersion(Pcap.VERSION);
//
//    /*
//     * Automatically close Pcap resource when done and checks the client and
//     * installed runtime API versions to ensure they are compatible.
//     */
//    try (NetPcap pcap = NetPcap.openOffline(PCAP_FILE)) { // Pro API
//
//      /* Set a pretty print formatter to toString() method */
//      pcap.setPacketFormatter(new PacketFormat());
//
//      /* Number of packets to capture */
//      final int PACKET_COUNT = 10;
//
//      /* Create protocol headers and reuse inside the dispatch handler */
//      final Ethernet ethernet = new Ethernet();
//      final Ip4 ip4 = new Ip4();
//      final Tcp tcp = new Tcp();
//      final Ip4RouterOption router = new Ip4RouterOption();
//
//      /* Capture packets and access protocol headers */
//      pcap.dispatch(PACKET_COUNT, (String user, Packet packet) -> { // Pro API
//
//        // If present, printout ethernet header
//        if (packet.hasHeader(ethernet))
//          System.out.println(ethernet);
//        
//        // If present, printout ip4 header
//        if (packet.hasHeader(ip4))
//          System.out.println(ip4);
//
//        // If present, printout IPv4.router header extension
//        if (packet.hasHeader(ip4) && ip4.hasExtension(router))
//          System.out.println(router);
//
//        // If present, printout tcp header
//        if (packet.hasHeader(tcp))
//          System.out.println(tcp);
//
//      }, "Example2 - Hello World");
//    }
//  }
}
