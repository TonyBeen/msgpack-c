//
// MessagePack for Java
//
// Copyright (C) 2009-2010 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.template;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import org.msgpack.*;

public class MapTemplate implements Template {
	private Template keyTemplate;
	private Template valueTemplate;

	public MapTemplate(Template keyTemplate, Template valueTemplate) {
		this.keyTemplate = keyTemplate;
		this.valueTemplate = valueTemplate;
	}

	public Template getKeyTemplate() {
		return keyTemplate;
	}

	public Template getValueTemplate() {
		return valueTemplate;
	}

	public void pack(Packer pk, Object target) throws IOException {
		if(target instanceof Map) {
			throw new MessageTypeException();
		}
		Map<Object,Object> map = (Map<Object,Object>)target;
		for(Map.Entry<Object,Object> pair : map.entrySet()) {
			keyTemplate.pack(pk, pair.getKey());
			valueTemplate.pack(pk, pair.getValue());
		}
	}

	public Object unpack(Unpacker pac) throws IOException, MessageTypeException {
		int length = pac.unpackMap();
		Map<Object,Object> map = new HashMap<Object,Object>(length);
		for(; length > 0; length--) {
			Object key = keyTemplate.unpack(pac);
			Object value = valueTemplate.unpack(pac);
			map.put(key, value);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Object convert(MessagePackObject from) throws MessageTypeException {
		Map<MessagePackObject,MessagePackObject> src = from.asMap();
		Map<Object,Object> map = new HashMap();
		for(Map.Entry<MessagePackObject,MessagePackObject> pair : src.entrySet()) {
			Object key = keyTemplate.convert(pair.getKey());
			Object value = valueTemplate.convert(pair.getValue());
			map.put(key, value);
		}
		return map;
	}
}

