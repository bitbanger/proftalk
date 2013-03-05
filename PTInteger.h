#ifndef PTINTEGER_H
#define PTINTEGER_H

#include "PTObject.h"
#include "Env.h"
#include <string>
class PTInteger : PTObject {
	public:
		PTInteger(int value);
		virtual const PTObject* eval(const Env *env) const;
	private:
		int value;
};

#endif
