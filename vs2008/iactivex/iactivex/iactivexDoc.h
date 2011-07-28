// iactivexDoc.h : interface of the CiactivexDoc class
//


#pragma once


class CiactivexDoc : public CDocument
{
protected: // create from serialization only
	CiactivexDoc();
	DECLARE_DYNCREATE(CiactivexDoc)

// Attributes
public:

// Operations
public:

// Overrides
public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);

// Implementation
public:
	virtual ~CiactivexDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	DECLARE_MESSAGE_MAP()
};


