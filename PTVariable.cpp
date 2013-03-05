#include "PTVariable.h"
PTVariable::PTVariable(std::string &id) : id(id) {}
const PTObject* PTVariable::eval(const Env *env) const {
	const PTObject *result = (*env)[id];
	if (result) return result;
	else {
		//Error?
		return NULL;
	}
}
