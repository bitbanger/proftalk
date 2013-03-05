#ifndef ENV_H
#define ENV_H
#include <map>
#include <string>
#include "PTObject.h"

class Env {
	private:
		typedef std::map<const std::string, const PTObject*> store_t;
		store_t store;
		Env *parent_env;
	
	public:
		Env();
		Env(Env *parent);
		const PTObject* operator[] (const std::string& key) const;
		void insert(const std::string& key, const PTObject* val);
};
#endif
