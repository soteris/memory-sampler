LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Inform the build tool of our module name and source file(s)
LOCAL_MODULE := ndkfoo
LOCAL_SRC_FILES := ndkfoo.c

include $(BUILD_SHARED_LIBRARY)