/* Group manipulation of Ndefs */
AbstractNdefCollection {

}
NGroup {
	var <>defs;

	*new {| ... aKeys|
		^super.new.init(aKeys)
	}
	
	init {|aKeys|
		this.defs = ();
		this.add(*aKeys);
	}

	defNames {
		^this.defs.asArray.collect{ |def| def.key }
	}

	size { ^defs.size }

	// Management
	add {| ... aDefNames|
		aDefNames.do{|key|
			this.defs[key] = Ndef(key);
		};
	}

	remove {| ... aDefNames|
		aDefNames.do{|key|
			this.defs.removeAt(key);
		};
	}

	removeAll {
		this.defNames.do{|key|
			this.remove(key)
		}
	}
	
	//Access
	at {| aDefName|
		^defs.at(aDefName.asSymbol);
	}

	do {|func|
		defs.do{|def|
			func.value(def);
		}
	}

	//Source Setting
	clear {| ... arglist|
		this.do{|def|
			def.clear(*arglist)
		}
	}

	fadeTime_ {| ... arglist|
		this.do{|def|
			def.fadeTime_(*arglist)
		}
	}

	//Initialisation
	ar {| ... arglist|
		this.do{|def|
			def.ar(*arglist)
		}
	}

	kr {| ... arglist|
		this.do{|def|
			def.kr(*arglist)
		}
	}

	// Play Controls
	play {| ... arglist|
		this.do{|def|
			def.play(*arglist)
		}
	}

	stop {| ... arglist|
		this.do{|def|
			def.stop(*arglist)
		}
	}

	//Timing
	clock {| ... arglist|
		this.do{|def|
			def.clock(*arglist)
		}
	}

	quant {| ... arglist|
		this.do{|def|
			def.quant(*arglist)
		}
	}

	printOn {|stream|

		var string = "";

		this.defNames.do {|name|
			string = string + ("\n\t Ndef('" ++ name.asString ++ "')") 
		};
		stream << this.class.asString << ":" << string 

	}

	
}


/* ------------------------------------------------------------------------
------------------------------------------------------------------------- */


NdefG : NGroup {

	classvar <>all;
	var <>key;
	
	*initClass { all = () }

	*new {|groupKey ... aKeys|
		var result;
		groupKey = groupKey.asSymbol;
		result = this.all.at(groupKey);

		if (result.isNil)
		{
			result = super.new.init(groupKey, *aKeys.postln);
			all[groupKey] = result;
		} 

		^this.all[groupKey]
	}

	init {|groupKey ... aKeys|
		super.init(*aKeys);
		this.key = groupKey;
	}

}


/* ------------------------------------------------------------------------
------------------------------------------------------------------------- */

NDict : NGroup {

	add {| ... aKeyDefnamePairs|
		if (aKeyDefnamePairs.size.even)
		{
			aKeyDefnamePairs.pairsDo {|key, def|
				this.defs[key.asSymbol] = Ndef(def.asSymbol);
			}
		} {
			^Error("Array Not Even").throw;
		}
	}

	printOn {|stream|
		var string = "";
		this.defs.pairsDo {|key, def|
			string = string + ("\n\t \\" ++ key.asString ++ " : Ndef('" ++ def.key.asString ++ "')") 
		};
		stream << this.class.asString << ":" << string 
	}

	//Access
	do {|func|
		defs.pairsDo{|key, def|
			func.value(key, def);
		}
	}

}

/* ------------------------------------------------------------------------
------------------------------------------------------------------------- */
/* Probably should inherit from node proxy */

NdefGroupMixer {
	var <>proxy, <>dest, <>vols;

	*new {|aDestNodeProxy|
		^super.new.init(aDestNodeProxy)
	}

	init {|aDestNodeProxy|
		this.dest = Ndef(aDestNodeProxy);
		this.proxy = NodeProxy.perform(
			this.dest.rate, 
			Server.default,
			numChannels: this.dest.numChannels.postln);
				// .bus_(this.dest.bus);

		this.vols = ();
	}

	put {| ... aKeyDefPairs|

		aKeyDefPairs.pairsDo{|key, def|
			var vol;
			vol = vols.at(key);
			if (vol.isNil)
			{
				vols[key] = NodeProxy.control.source_(1);
			};
			proxy.put(
				key, 
				{ def.ar * vols[key].kr }
			)
		}
	}

	remove {|aKeys, aFadeTime|
		aKeys.do{|aKey|
			proxy.at(aKey).clear(aFadeTime)
		}
	}

	// Setting
	vol {|aKey, aVal|
		vols[aKey].source_(aVal)
	}

	// Access
	at {|aKey|
		^proxy[aKey]
	}

	// Cleanup

	removeAt {|aKey|
		/* fadeTime ?  */
		proxy.at(aKey).clear;
		vols.removeAt(aKey).clear;	
	}

	free {
		vols.clear;
		proxy.clear;
	}

}



















