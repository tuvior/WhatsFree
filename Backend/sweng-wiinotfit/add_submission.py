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
    # should add time + duration field


class AddSubmission(webapp2.RequestHandler):
	def get(self):
		subName = self.request.get('name')
        subCategory = self.request.get('category')
		subDescription = self.request.get('description')
		subLocation = self.request.get('location')
		subImage = str(self.request.get('image'))
		subKeywords = self.request.get('keywords')
        subRating = self.request.get('rating')
        cookie = self.request.get('cookie')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

		# Name, Category, Location and Image are the required fields. Time will also be a required field once created
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
                submission = Submission(name = subName, category = subCategory, description = subDescription, location = subLocation, image = subImage, keywords = subKeywords, rating = subRating, submitter = session.user)
                submission.put()
                self.response.write(json_response(0))
            else:
                self.response.write(json_response(6))


app = webapp2.WSGIApplication([
    ('/new_submission', AddSubmission),
], debug=True)