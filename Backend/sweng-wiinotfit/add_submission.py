import webapp2
import string
from google.appengine.ext import ndb


def json_response(status):
    if status == 0:
        res = """{
    "submission": {
        "status": "ok"
    }
}"""

    elif status == -1:
        res = """{
    "login": {
        "status": "failure",
        "reason": "Name"
    }
}"""

    elif status == -2:
        res = """{
    "login": {
        "status": "failure",
        "reason": "Category"
    }
}"""

    elif status == -3:
        res = """{
    "login": {
        "status": "failure",
        "reason": "Location"
    }
}"""

    elif status == -4:
        res = """{
    "login": {
        "status": "failure",
        "reason": "Location"
    }
}"""

    return res


class Submission(ndb.Model):
	name = ndb.StringProperty()
    category = ndb.StringProperty()
	description = ndb.StringProperty()
	location = ndb.StringProperty()
	image = ndb.BlobProperty()
	keywords = ndb.StringProperty()
    # should add time + duration field
    # should add rating of submission


class AddSubmission(webapp2.RequestHandler):
	def get(self):
		subName = self.request.get('name')
        subCategory = self.request.get('category')
		subDescription = self.request.get('description')
		subLocation = self.request.get('location')
		subImage = self.request.get('image')
		subKeywords = self.request.get('keywords')

		# Name, Category, Location and Image are the required fields. Time will also be a required field once created
		if not subName:
			self.response.write(json_response(-1))
            
        elif not subCategory:
            self.response.write(json_response(-2))
            
        elif not subLocation:
            self.response.write(json_response(-3))
            
        elif not subImage:
            self.response.write(json_response(-4))
        

		submission = Submission(name = subName, category = subCategory, description = subDescription, location = subLocation, image = subImage, keywords = subKeywords)
		submission.put()

app = webapp2.WSGIApplication([
    ('/new_submission', AddSubmission),
], debug=True)