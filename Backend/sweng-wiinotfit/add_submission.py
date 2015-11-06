import webapp2
import string
import json
from google.appengine.ext import ndb


def json_response(status):
    if status == 0:
        res = """{
    "submission": {
        "status": "ok"
    }
}"""

    elif status == 1:
        res = """{
    "submission": {
        "status": "failure",
        "reason": "name"
    }
}"""

    elif status == 2:
        res = """{
    "submission": {
        "status": "failure",
        "reason": "category"
    }
}"""

    elif status == 3:
        res = """{
    "submission": {
        "status": "failure",
        "reason": "location"
    }
}"""

    elif status == 4:
        res = """{
    "submission": {
        "status": "failure",
        "reason": "image"
    }
}"""
    elif status == 5:
        res = """{
    "submission": {
        "status": "failure",
        "reason": "cookie"
    }
}"""
    elif status == 6:
        res = """{
    "submission": {
        "status": "failure",
        "reason": "session"
    }
}"""

    return res

class Session(ndb.Model):
    cookie = ndb.StringProperty()
    user = ndb.StringProperty()

class Submission(ndb.Model):
	name = ndb.StringProperty()
    category = ndb.StringProperty()
	description = ndb.StringProperty()
	location = ndb.StringProperty()
	image = ndb.BlobProperty()
	keywords = ndb.StringProperty()
    rating = ndb.IntegerProperty()
    submitter = ndb.StringProperty()
    submitted = ndb.DateTimeProperty(auto_now_add=True)
    tfrom = ndb.DateTimeProperty()
    tto = ndb.DateTimeProperty()
    # should add time + duration field


class addSubmission(webapp2.RequestHandler):
	def post(self):
		subName = self.request.get('name')
        subCategory = self.request.get('category')
		subDescription = self.request.get('description')
		subLocation = self.request.get('location')
		subImage = str(self.request.get('image'))
		subKeywords = self.request.get('keywords')
        cookie = self.request.get('cookie')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

		# Name, Category, Location, Image and Cookie are the required fields. 
        if not cookie:
            self.response.write(json_response(5))
            
		elif not subName:
			self.response.write(json_response(1))
            
        elif not subCategory:
            self.response.write(json_response(2))
            
        elif not subLocation:
            self.response.write(json_response(3))
            
        elif not subImage:
            self.response.write(json_response(4))
            
        else:
            session = Session.query(Session.cookie == cookie).get()
            if session:
                submission = Submission(name = subName, category = subCategory, description = subDescription, location = subLocation, image = subImage, keywords = subKeywords, rating = 0, submitter = session.user)
                submission.put()
                
                # Give back submission under json format in order to display it as soon as it is created
                response = {'name': subName, 'category': subCategory, 'description': subDescription, 'location': subLocation, 'image': subImage, 'keywords': subKeywords}
                json_response = json.dumps(response)     
                self.response.write(json_response)           
                # self.response.write(json_response(0))
            else:
                self.response.write(json_response(6))


app = webapp2.WSGIApplication([
    ('/new_submission', addSubmission),
], debug=True)