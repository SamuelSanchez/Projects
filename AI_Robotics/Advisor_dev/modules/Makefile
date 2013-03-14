# Toolchain/Environment
export SHELL 	= /bin/bash
export CPP    	= g++
DEBUG           = -g

PROJ_DIR	= ../..
PROJ_LIB_DIR	= $(PROJ_DIR)/lib
LIB_DIR 	= ../lib

# Dependency: MetroUtil
METROUTIL     	= $(PROJ_LIB_DIR)/libMetrobotics.a
METROUTIL_DIR	= $(PROJ_DIR)/utils/MetroUtil
METROUTIL_LIB 	= $(PROJ_LIB_DIR)
METROUTIL_INC 	= $(METROUTIL_DIR)/include


AUCTIONMANAGER		= ./AuctionManager
AUCTIONMANAGER_SRC 	= $(AUCTIONMANAGER)/src
AUCTIONMANAGER_INC	= $(AUCTIONMANAGER)/include

VISDEBUG	= ./VisualDebugger
VISDEBUG_SRC	= $(VISDEBUG)/src
VISDEBUG_INC	= $(VISDEBUG)/include

BEHAVIOR	= ./Behavior
BEHAVIOR_SRC	= $(BEHAVIOR)/src
BEHAVIOR_INC	= $(BEHAVIOR)/include

COMMUNICATION		= ./Communication
COMMUNICATION_SRC	= $(COMMUNICATION)/src
COMMUNICATION_INC	= $(COMMUNICATION)/include

VPATH 		= $(VISDEBUG_SRC) $(AUCTIONMANAGER_SRC) $(BEHAVIOR_SRC) $(COMMUNICATION_SRC)

# Options
INC_FLAGS	= -I$(AUCTIONMANAGER_INC) -I$(VISDEBUG_INC) -I$(BEHAVIOR_INC) -I$(METROUTIL_INC) -I$(COMMUNICATION_INC)
CPPFLAGS 	= $(INC_FLAGS) `pkg-config --cflags playerc++` `pkg-config --cflags opencv`
CXXFLAGS	= -c $(DEBUG) #-Wall
#LIBS   		= -L$(METROUTIL_LIB) -lMetrobotics -L$(LOCALIZE_LIB) -lLocalization -L$(PATHPLAN_LIB) -lPathPlanner -lboost_system-mt -lglut -lGLU `pkg-config --libs playerc++` `pkg-config --libs opencv`
LIBS   		= -L$(METROUTIL_LIB) -lMetrobotics -L$(LOCALIZE_LIB) -lLocalization -L$(PATHPLAN_LIB) -lPathPlanner -lpthread -lboost_system-mt -lGL -lglut -lGLU `pkg-config --libs playerc++` `pkg-config --libs opencv`

# Files
OUTPUT_DIR	= ..
OUTPUT    	= TaskManager

SOURCE_PATHS 	= $(shell find $(VISDEBUG_SRC) -name "*cpp") $(shell find $(AUCTIONMANAGER_SRC) -name "*cpp") $(shell find $(BEHAVIOR_SRC) -name "*cpp") $(shell find $(COMMUNICATION_SRC) -name "*cpp") $(wildcard *.cpp)
SRCS 		:= $(foreach source, $(SOURCE_PATHS), $(subst $(VISDEBUG_SRC)/, , $(source) ))
SRCS 		:= $(foreach source, $(SRCS), $(subst $(AUCTIONMANAGER_SRC)/, , $(source) ))
SRCS 		:= $(foreach source, $(SRCS), $(subst $(BEHAVIOR_SRC)/, , $(source) ))
SRCS 		:= $(foreach source, $(SRCS), $(subst $(COMMUNICATION_SRC)/, , $(source) ))
SOURCES		:= $(SRCS)
SOURCE_DIRS 	= $(dir $(SOURCE_PATHS))

# Object files to link with no path
OBJECTS 	= $(notdir $(SOURCES:.cpp=.o))

################### General targets #########################
.PHONY: $(OUTPUT) all clean purge docs display

all: $(OUTPUT) metroutil 
	mv $(OUTPUT) $(OUTPUT_DIR)

metroutil: $(METROUTIL)


display:
	@echo Sourcepaths: $(SOURCE_PATHS)
	@echo srcs: $(SRCS)
	@echo sources: $(SOURCES)


########################################################

# build metroutil, localization, pathplanner and the controller
$(OUTPUT): $(METROUTIL) $(OBJECTS)
	$(CPP) -o $@ $(OBJECTS) $(LIBS)

# Dependency: MetroUtil
$(METROUTIL):
	@echo Building MetroUtil
	@$(MAKE) -e --directory=$(METROUTIL_DIR)
	@if [ "$$?" != 0 ]; then \
		echo "MetroUtil failed to build"; \
		exit 1; \
	fi
	@echo Deploying library
	@$(MAKE) -e --directory=$(METROUTIL_DIR) deploy

# Remove unnecessary output files
clean:
	@$(MAKE) -e --directory=$(METROUTIL_DIR) clean
	rm -rf $(OUTPUT_DIR)/$(OUTPUT)
	rm -rf $(OBJECTS)

# Remove all output files
purge:
	@$(MAKE) -e clean
	rm -rf $(OUTPUT_DIR)/$(OUTPUT)
	rm -rf $(LIB_DIR)/*
	rm -rf $(PROJ_LIB_DIR)/*

