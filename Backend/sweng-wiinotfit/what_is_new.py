import webapp2
import string
import json
from google.appengine.ext import ndb


def json_response(status):
    if status == 0:
        res = """{
    "whatIsNew": {
        "status": "ok"
    }
}"""

    elif status == -1:
        res = """{
    "whatIsNew": {
        "status": "invalid"
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
    rating = ndb.IntegerProperty()
    submitter = ndb.StringProperty()
    submitted = ndb.DateTimeProperty(auto_now_add=True)
    tfrom = ndb.DateTimeProperty()
    tto = ndb.DateTimeProperty()
    
    
class whatIsNew(webapp2.RequestHandler):
    def post(self):
        
        # may be useless because number and time range probably fixed, for now keep it
        what_is_new_flag = self.request.get('flag')
        what_is_new_number = self.request.get('number')
        what_is_new_time_range = self.request.get('range')
        
        if not what_is_new_flag:
            self.response.write(json_response(-1))
        
        if not what_is_new_number:
            self.response.write(json_response(-1)) 
        
        if not what_is_new_time_range:
            self.response.write(json_response(-1))
        
        submissions = Submission.query(Submission.submitted <= what_is_new_time_range)
        
        if what_is_new_number < submissions.len():
            for i in range(0, what_is_new_number):
                submission = Submission.query(Submission.submitted <= what_is_new_time_range).fetch(i)
                # create json response with all submissions, for now just OK response
                self.response.write(json_response(0))
        
        else:
            for i in range(0, submissions.len()):
                submission = Submission.query(Submission.submitted <= what_is_new_time_range).fetch(i)
                # create json response with all submissions, for now just OK response
                self.response.write(json_response(0))
        
app = webapp2.WSGIApplication([
    ('/news', whatIsNew),
], debug=True)