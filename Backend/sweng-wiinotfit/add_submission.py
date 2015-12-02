import webapp2
import string
import json
import math
from google.appengine.ext import ndb
from submission import Submission
from session import Session

def json_response(status, id):
    if status == 0:
        res = """{
    "submission": {
        "status": "ok"
        "id": \"""" + str(id) + """\"
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

class addSubmission(webapp2.RequestHandler):
    def get(self):
        subName = self.request.get('name')
        subCategory = self.request.get('category')
        subDescription = self.request.get('description')
        subLocation = self.request.get('location')
        subImage = str(self.request.get('image'))
        subKeywords = self.request.get('keywords')
        cookie = self.request.get('cookie')
        subTfrom = self.request.get('from')
        subTto = self.request.get('to')
        subLatitude = self.request.get('latitude')
        subLongitude = self.request.get('longitude')
        inf = float("inf")

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

        if not subLatitude:
            subLatitude = inf
        if not subLongitude:
            subLongitude = inf

        # Name, Category, Location, Image and Cookie are the required fields. 
        if not cookie:
            self.response.write(json_response(5, 0)) 
        elif not subName:
            self.response.write(json_response(1, 0))
        elif not subCategory:
            self.response.write(json_response(2, 0))
        elif not subLocation:
            self.response.write(json_response(3, 0))
        elif not subImage:
            self.response.write(json_response(4, 0))
            
        else:
            session = Session.query(Session.cookie == cookie).get()
            if session:
                submission = Submission(name = subName, category = subCategory, description = subDescription, location = subLocation, image = subImage, 
                                        keywords = subKeywords, rating = 0, submitter = session.user, latitude = subLatitude, longitude = subLongitude)
                submission_key = submission.put()      
                submission_id = submission_key.id()
                self.response.write(json_response(0, submission_id))
                
            else:
                self.response.write(json_response(6, 0))


app = webapp2.WSGIApplication([
    ('/submission', addSubmission),
], debug=True)