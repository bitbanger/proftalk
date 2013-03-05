#ifndef ENV_H
#define ENV_H
#include <map>
#include <string>
#include "PTObject.h"

class Env {
	private:
		std::map<const std::string, const PTObject*> store;
	
	public:
		const PTObject* operator[] (const std::string& key) const;
		void insert(const std::string& key, const PTObject* val);
};
#endif
