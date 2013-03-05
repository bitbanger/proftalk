#ifndef PTVARIABLE_H
#define PTVARIABLE_H

#include "PTObject.h"
#include "Env.h"
#include <string>
class PTVariable : PTObject {
	public:
		PTVariable(std::string &id);
		virtual const PTObject* eval(const Env *env) const;
	private:
		std::string id;
};

#endif
