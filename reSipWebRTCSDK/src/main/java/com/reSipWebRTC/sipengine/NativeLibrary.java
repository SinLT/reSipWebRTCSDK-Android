/*
 *  Copyright (c) 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.reSipWebRTC.sipengine;

import android.util.Log;

public class NativeLibrary {
  private static String TAG = "NativeLibrary";

  static class DefaultLoader implements NativeLibraryLoader {
    @Override
    public boolean load(String name) {

      Log.d(TAG, "Loading library: " + "c++_shared");
      try {
        System.loadLibrary("c++_shared");
      } catch (UnsatisfiedLinkError e) {
        Log.e(TAG, "Failed to load native library: " + "c++_shared", e);
        return false;
      }
      Log.d(TAG, "Loaded library: " + "c++_shared");

      Log.d(TAG, "Loading library: " + name);
      try {
        System.loadLibrary(name);
      } catch (UnsatisfiedLinkError e) {
        Log.e(TAG, "Failed to load native library: " + name, e);
        return false;
      }
      Log.d(TAG, "Loaded library: " + name);
      return true;
    }
  }

  private static Object lock = new Object();
  private static boolean libraryLoaded;

  /**
   * Loads the native library. Clients should call PeerConnectionFactory.initialize. It will call
   * this method for them.
   */
  static void initialize(NativeLibraryLoader loader, String libraryName) {
    synchronized (lock) {
      if (libraryLoaded) {
        Log.d(TAG, "Native library has already been loaded.");
        return;
      }
      libraryLoaded = loader.load(libraryName);
    }
  }

  /** Returns true if the library has been loaded successfully. */
  static boolean isLoaded() {
    synchronized (lock) {
      return libraryLoaded;
    }
  }
}
