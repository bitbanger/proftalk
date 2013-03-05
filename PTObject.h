#ifndef PTOBJECT_H
#define PTOBJECT_H

class Env;
class PTObject {
	public:
		//not sure what this needs to do, exactly, yet
		virtual const PTObject* eval(const Env *env) const = 0;
};

#endif
