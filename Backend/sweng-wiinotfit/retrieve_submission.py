import webapp2
import string
import json
from google.appengine.ext import ndb
from submission import Submission
from session import Session
import datetime

def json_response(status):
    if status == -1:
        res = """{
    "retrieve": {
        "status": "invalid"
    }
}"""    
    
    elif status == 1:
        res = """{
    "retrieve": {
        "status": "failure",
        "reason": "name"
    }
}"""

    return res

# check which arguments are there 
def json_string(name, category , description , location , image , keywords, submitter, tfrom, tto):
    json_string = {'name': name,'category': category, 'description': description, 'location': location, 'image': image,
                   'keywords': keywords, 'submitter': submitter, 'from': tfrom, 'to': tto}
    return json_string


def json_error(status, reason):
    json_string = {'status': status, 'reason': reason}
    return json_string

# add cookie
class retrieveSubmission(webapp2.RequestHandler):
    def get(self):
        flag = self.request.get('flag')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
        
        if not flag:
            self.response.write(json_response(-1))

        # flag = 1 means a single request
        elif flag == '1':
            name = self.request.get('name');

            if not name:
                self.response.write(json_error('error', 'name'))

            else:
                submission = Submission.query(Submission.name == name).get()
                
                if not submission:
                    error = json_error('failure', 'no corresponding submission')
                    self.response.write(error)

                else:
                    string_submission = json_string(submission.name, submission.category, submission.description, submission.location,
                                       submission.image, submission.keywords, submission.submitter, submission.tfrom, submission.tto)
                    response = json.dumps(string_submission)
                    self.response.write(response)

        # flag = 2 means that we are requesting submissions to display in what's new
        elif flag == '2':
            # for now
            time_range = 1447786800
            date = datetime.datetime.fromtimestamp(time_range/1e3)
            submissions_number = 5

            submissions = Submission.query(Submission.submitted >= date).fetch(submissions_number)

            if not submissions:
                error = json_error('failure', 'nothing in range')
                self.response.write(error)

            else:
                submissions_array = []
                if(submissions_number <= len(submissions)):
                    for i in range(0, submissions_number):
                        # which one?
                        # submission = Submission.query(Submission.submitted <= what_is_new_time_range).fetch(i)
                        submission = submissions[i]
                        # use thumbnail
                        json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                        submissions_array.append(json_submission)

                else:
                    for i in range(0, len(submissions)):
                        submission = submissions[i]
                        json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                        submissions_array.append(json_submission)

                response = json.dumps(submissions_array)
                self.response.write(response)
        
        # flag = 3 means that we are requesting submissions for around you

        #flag = 4 means that we are requesting submissions for a specific category


        # every other flag generate an error
        else:
            error = json_error('failure', 'flag')
            self.response.write(error)


app = webapp2.WSGIApplication([
    ('/retrieve', retrieveSubmission),
], debug=True)